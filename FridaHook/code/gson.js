function hookGson() {
    Java.perform(function () {
        let Gson = Java.use("com.google.gson.Gson");
        Gson["fromJson"].overload('java.lang.String', 'java.lang.Class').implementation = function (str, cls) {
            console.log(`Gson.fromJson is called: str=${str}, cls=${cls}`);
            let result = this["fromJson"](str, cls);
            return result;
        };
    })
}


export { hookGson }