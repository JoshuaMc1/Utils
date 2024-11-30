<?php

namespace PHP\Cache;

use PHP\Cache\Drivers\{
    DatabaseCacheDriver,
    FileCacheDriver
};

class Cache
{
    private static $instance;
    private $config;
    private $driverInstance;

    private function __construct()
    {
        $this->config = require __DIR__ . '/../Config/config.php';
        $this->initializeDriver();
    }

    public static function getInstance()
    {
        if (self::$instance === null) {
            self::$instance = new self();
        }

        return self::$instance;
    }

    public static function __callStatic($name, $arguments)
    {
        $instance = self::getInstance();

        if (!method_exists($instance->driverInstance, $name)) {
            throw new \BadMethodCallException("Método '{$name}' not found.");
        }

        return call_user_func_array([$instance->driverInstance, $name], $arguments);
    }

    private function initializeDriver()
    {
        $driver = $this->config['driver'];
        $drivers = $this->config['drivers'];

        if (!isset($drivers[$driver])) {
            throw new \Exception("Driver '{$driver}' not found.");
        }

        $driverConfig = $drivers[$driver];

        switch ($driver) {
            case 'file':
                $this->driverInstance = new FileCacheDriver($driverConfig);
                break;

            case 'database':
                $this->driverInstance = new DatabaseCacheDriver($driverConfig);
                break;

            default:
                throw new \Exception("Driver '{$driver}' not supported.");
        }
    }

    public function set($key, $value, $ttl = null)
    {
        return $this->driverInstance->set($key, $value, $ttl);
    }

    public function get($key)
    {
        return $this->driverInstance->get($key);
    }

    public function delete($key)
    {
        return $this->driverInstance->delete($key);
    }

    public function clear()
    {
        return $this->driverInstance->clear();
    }
}
