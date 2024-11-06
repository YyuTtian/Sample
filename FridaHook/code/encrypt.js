
import { getJavaUtil } from "./java_util"


function checkEncrypt() {
    Java.perform(function () {

        let javaUtil = getJavaUtil()

        let Cipher = Java.use("javax.crypto.Cipher")
        let overloadsArr = Cipher.doFinal.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("Cipher.doFinal is called! params is: ", params);
                let result = this.doFinal.apply(this, arguments);
                console.log(javaUtil.showStack());
                return result;
            }
        }

        let Mac = Java.use("javax.crypto.Mac")
        overloadsArr = Mac.doFinal.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("Mac.doFinal is called! params is: ", params);
                let result = this.doFinal.apply(this, arguments);
                console.log(javaUtil.showStack());
                return result;
            }
        }

        let MessageDigest = Java.use("java.security.MessageDigest")
        overloadsArr = MessageDigest.getInstance.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("MessageDigest.getInstance is called! params is: ", params);
                let result = this.getInstance.apply(this, arguments);
                console.log(javaUtil.showStack());
                return result;
            }
        }

        let Signature = Java.use("java.security.Signature")
        overloadsArr = Signature.getInstance.overloads;
        for (let i = 0; i < overloadsArr.length; i++) {
            overloadsArr[i].implementation = function () {
                let params = "";
                for (let j = 0; j < arguments.length; j++) {
                    params += arguments[j] + " ";
                }
                console.log("Signature.getInstance is called! params is: ", params);
                let result = this.getInstance.apply(this, arguments);
                console.log(javaUtil.showStack());
                return result;
            }
        }
    })
}

export { checkEncrypt }