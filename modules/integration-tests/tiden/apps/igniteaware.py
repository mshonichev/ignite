

from tiden.apps import JavaApp
from tiden.apps.ignite import Ignite


class Igniteaware(JavaApp):

    ignite_service_main_class = 'org.apache.ignite.internal.ducktest.utils.IgniteApplicationService'

    default_jvm_options = JavaApp.default_jvm_options + [
        "-Dlog4j.configDebug=true",
        "-Xmx1G",
        "-ea",
        "-DIGNITE_ALLOW_ATOMIC_OPS_IN_TX=false",
        # "-DIGNITE_SUCCESS_FILE=" + self.PERSISTENT_ROOT + "/success_file "
    ]

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.num_nodes = kwargs.get('num_nodes', 1)
        self.params = ''
        self.ignite: Ignite = None
        self.java_class_name = kwargs['java_class_name']

    def start(self, ignite: Ignite, params=''):
        self.params = params
        self.ignite = ignite
        self.start_nodes()

    def get_node_start_commands(self, node_idx):
        ignite_home = self.ignite.client_ignite_home
        ignite_config = f'{self.ignite.remote_test_module_dir}/client_default.xml'
        jvm_opts = self.get_node_jvm_options(node_idx)
        app_args = ','.join([self.java_class_name, ignite_config, self.params])
        node_log = self.nodes[node_idx]['log']
        commands = [
            f'cd {ignite_home}',
        ]
        env = self.get_node_env(node_idx)
        if env:
            commands.extend([f'export {env_name}=\"{env_value}\"' for env_name, env_value in env.items()])
        commands.extend([
            f'nohup bin/ignite.sh {jvm_opts} {app_args} 1>{node_log} 2>&1 & '
            f'(n=0; '
            f' while [ $n -le 5 ]; do '
            f'   sleep 0.5s; '
            f'   n=$[n+1]; '
            f'   res=$(jps -m | grep "{app_args}" | cut -d " " -f 1); '
            f'   if [ ! "$res" = "" ]; then '
            f'     echo $res; '
            f'     break;'
            f'   fi; '
            f' done'
            f')'
        ])
        return [';'.join(commands)]

    def get_node_jvm_options(self, node_idx):
        return ' '.join('-J' + option for option in self.jvm_options)

    def get_node_env(self, node_idx):
        return {
            'MAIN_CLASS': Igniteaware.ignite_service_main_class,
            'EXCLUDE_TEST_CLASSES': 'true',
            'USER_LIBS': self.config['artifacts']['igniteaware']['remote_path'],
            # 'IGNITE_LOG_DIR': self.ignite.remote_test_dir,
            'NODE_IP': self.nodes[node_idx]['host'],
        }

    def stop_nodes(self, *args):
        super().stop_nodes(*args)
        self.wait_message('IGNITE_APPLICATION_FINISHED')

    def run(self, ignite: Ignite, params=''):
        self.start(ignite, params=params)
        self.wait_message('IGNITE_APPLICATION_INITIALIZED')
        self.stop_nodes()

    def extract_result(self, result_name):
        self.grep_log(
            0,
            result={
                'remote_regex': result_name + '->',
                'local_regex': f'{result_name}->(.*)<-'
            }
        )
        return self.nodes[0]['result']
