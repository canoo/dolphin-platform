// Browsers to run on Sauce Labs

var browsers = {
    sl_winXP_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Windows XP',
        version: '46.0'
    },
    sl_winXP_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'Windows XP',
        version: '42.0'
    },

    sl_win7_ie9: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 7',
        version: '9.0'
    },
    sl_win7_ie10: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 7',
        version: '10.0'
    },
    sl_win7_ie11: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 7',
        version: '11.0'
    },
    sl_win7_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Windows 7',
        version: '46.0'
    },
    sl_win7_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'Windows 7',
        version: '42.0'
    },

    sl_win8_0_ie10: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 8',
        version: '10.0'
    },
    sl_win8_0_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Windows 8',
        version: '46.0'
    },
    sl_win8_0_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'Windows 8',
        version: '42.0'
    },

    sl_win8_1_ie11: {
        base: 'SauceLabs',
        browserName: 'internet explorer',
        platform: 'Windows 8.1',
        version: '11.0'
    },
    sl_win8_1_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Windows 8.1',
        version: '46.0'
    },
    sl_win8_1_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'Windows 8.1',
        version: '42.0'
    },

    sl_mac11_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'OS X 10.11',
        version: '46.0'
    },
    sl_mac11_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'OS X 10.11',
        version: '42.0'
    },
    sl_mac11_safari: {
        base: 'SauceLabs',
        browserName: 'safari',
        platform: 'OS X 10.11',
        version: '9.0'
    },

    sl_mac10_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'OS X 10.10',
        version: '46.0'
    },
    sl_mac10_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'OS X 10.10',
        version: '42.0'
    },
    sl_mac10_safari: {
        base: 'SauceLabs',
        browserName: 'safari',
        platform: 'OS X 10.10',
        version: '8.0'
    },

    sl_linux_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'Linux',
        version: '46.0'
    },
    sl_linux_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'Linux',
        version: '42.0'
    },
    sl_linux_opera: {
        base: 'SauceLabs',
        browserName: 'opera',
        platform: 'Linux',
        version: '12.15'
    },

    sl_ipad_8_4: {
        base: 'SauceLabs',
        browserName: 'Safari',
        appiumVersion: '1.4.16',
        deviceName: 'iPad Simulator',
        deviceOrientation: 'portrait',
        platformName: 'iOS',
        platformVersion: '8.4'
    },
    sl_android_5_1: {
        base: 'SauceLabs',
        browserName: 'Browser',
        appiumVersion: '1.4.16',
        deviceName: 'Android Emulator',
        deviceOrientation: 'portrait',
        platformName: 'Android',
        platformVersion: '5.1'
    }
    ,
    sl_mac9_chrome: {
        base: 'SauceLabs',
        browserName: 'chrome',
        platform: 'OS X 10.9',
        version: '46.0'
    }
    ,
    sl_mac9_firefox: {
        base: 'SauceLabs',
        browserName: 'firefox',
        platform: 'OS X 10.9',
        version: '42.0'
    },
    sl_mac9_safari: {
        base: 'SauceLabs',
        browserName: 'safari',
        platform: 'OS X 10.9',
        version: '7.0'
    },
    sl_ipad_8_3: {
        base: 'SauceLabs',
        browserName: 'Safari',
        appiumVersion: '1.6.3',
        deviceName: 'iPad Simulator',
        deviceOrientation: 'portrait',
        platformName: 'iOS',
        platformVersion: '8.3',
        flags: ['--disable-web-security']
    }
    // sl_ipad_9_2: {
    //     base: 'SauceLabs',
    //     browserName: 'Safari',
    //     appiumVersion: '1.6.3',
    //     deviceName: 'iPad Simulator',
    //     deviceOrientation: 'portrait',
    //     platformName: 'iOS',
    //     platformVersion: '9.2',
    //     flags: ['--disable-web-security']
    // }
    //
    // sl_ipad_9_1: {
    //     base: 'SauceLabs',
    //     browserName: 'Safari',
    //     appiumVersion: '1.6.3',
    //     deviceName: 'iPad Simulator',
    //     deviceOrientation: 'portrait',
    //     platformName: 'iOS',
    //     platformVersion: '9.1',
    //     flags: ['--disable-web-security']
    // }
    // ,

    // sl_android_5_0: {
    //     base: 'SauceLabs',
    //     browserName: 'Browser',
    //     appiumVersion: '1.5.3',
    //     deviceName: 'Android Emulator',
    //     deviceOrientation: 'portrait',
    //     platformName: 'Android',
    //     platformVersion: '5.0',
    //     flags: ['--disable-web-security']
    // },
    //
    // sl_android_4_4: {
    //     base: 'SauceLabs',
    //     browserName: 'Browser',
    //     appiumVersion: '1.5.3',
    //     deviceName: 'Android Emulator',
    //     deviceOrientation: 'portrait',
    //     platformName: 'Android',
    //     platformVersion: '4.4',
    //     flags: ['--disable-web-security']
    // }
};



exports.browsers = browsers;