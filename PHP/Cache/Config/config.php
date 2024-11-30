<?php

return [
    'driver' => 'file',
    'drivers' => [
        'file' => [
            'path' => sprintf('%s/%s', sys_get_temp_dir(), '.cache'),
            'ttl' => 3600,
            'prefix' => 'c_'
        ],
        'database' => [
            /**
             * Database configuration.
             * 
             * Defaults to sqlite in development and mysql in production.
             */
            'driver' => 'sqlite',
            'database' => sprintf('%s/%s', sys_get_temp_dir(), '/cache.sqlite'),
            // 'host' => '127.0.0.1',
            // 'port' => 3306,
            // 'username' => 'root',
            // 'password' => '',
            // 'charset' => 'utf8',
            // 'collation' => 'utf8_unicode_ci'
        ]
    ]
];
