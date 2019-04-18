﻿/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace Apache.Ignite.Core.Impl.Plugin
{
    using Apache.Ignite.Core.Plugin;

    /// <summary>
    /// Wraps user-defined generic <see cref="IPluginProvider{TConfig}"/>.
    /// </summary>
    internal class PluginProviderProxy<T> : IPluginProviderProxy where T : IPluginConfiguration
    {
        /** */
        private readonly T _pluginConfiguration;

        /** */
        private readonly IPluginProvider<T> _pluginProvider;

        /// <summary>
        /// Initializes a new instance of the <see cref="PluginProviderProxy{T}"/> class.
        /// </summary>
        public PluginProviderProxy(T pluginConfiguration, IPluginProvider<T> pluginProvider)
        {
            _pluginConfiguration = pluginConfiguration;
            _pluginProvider = pluginProvider;
        }

        /** <inheritdoc /> */
        public void Start(PluginProcessor pluginProcessor)
        {
            _pluginProvider.Start(new PluginContext<T>(pluginProcessor, _pluginConfiguration));
        }

        /** <inheritdoc /> */
        public string Name
        {
            get { return _pluginProvider.Name; }
        }

        /** <inheritdoc /> */
        public string Copyright
        {
            get { return _pluginProvider.Copyright; }
        }

        /** <inheritdoc /> */
        public TPlugin GetPlugin<TPlugin>() where TPlugin : class
        {
            return _pluginProvider.GetPlugin<TPlugin>();
        }

        /** <inheritdoc /> */
        public void Stop(bool cancel)
        {
            _pluginProvider.Stop(cancel);
        }

        /** <inheritdoc /> */
        public void OnIgniteStart()
        {
            _pluginProvider.OnIgniteStart();
        }

        /** <inheritdoc /> */
        public void OnIgniteStop(bool cancel)
        {
            _pluginProvider.OnIgniteStop(cancel);
        }
        
        /** <inheritdoc /> */
        public object Provider
        {
            get { return _pluginProvider; }
        }
    }
}
