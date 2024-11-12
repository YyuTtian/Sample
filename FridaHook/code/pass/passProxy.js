function passProxy() {
    Java.perform(function () {
        let clazz = Java.use("java.util.Properties");
        let overloadsArr = clazz.getProperty.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("clazz.getProperty is called! params is: ", params);
                let result = this.getProperty.apply(this, arguments);
                console.log("result:" + result);

                let key = arguments[0]
                if (key.indexOf("https.proxyHost") != -1 || key.indexOf("https.proxyPory") != -1 || key.indexOf("net.dns1") != -1) {
                    return null
                } else {
                    return result;
                }
            }
        }
    })
}

export { passProxy }