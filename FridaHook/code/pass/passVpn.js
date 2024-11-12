function passVpn() {
    Java.perform(function () {
        var can_hook = false
        var ConnectivityManager = Java.use("android.net.ConnectivityManager");
        ConnectivityManager.getNetworkInfo.overload('int').implementation = function () {
            console.log("call getNetworkInfo function =========!!!")
            if (arguments[0] == 17) {
                can_hook = true
            }
            var ret = this.getNetworkInfo(arguments[0])
            return ret
        }

        var NetworkInfo = Java.use("android.net.NetworkInfo")
        NetworkInfo.isConnected.implementation = function () {
            console.log("call isConnected ========= !!!")
            var ret = this.isConnected()
            if (can_hook) {
                ret = false
                can_hook = false
                console.log("call isConnected function========= !!!")
            }
            return ret
        }

        var NetworkCapabilities = Java.use("android.net.NetworkCapabilities")
        NetworkCapabilities.hasTransport.implementation = function () {
            console.log("call hasTransport =========!!!")
            var ret = this.hasTransport(arguments[0])
            if (arguments[0] == 4) {
                console.log("call hasTransport function =========!!!")
                ret = false
            }
            return ret
        }

        NetworkCapabilities.transportNameOf.overload('int').implementation = function () {
            console.log("call transportNameOf =========!!!")
            var ret = this.transportNameOf(arguments[0])
            if (ret.indexOf("VPN") >= 0) {
                ret = "WIFI"
            }
            return ret;
        }

        NetworkCapabilities.appendStringRepresentationOfBitMaskToStringBuilder.implementation = function (sb, bitMask, nameFetcher, separator) {
            if (bitMask == 18) {
                console.log("bitMask", bitMask);
                sb.append("WIFI");
            } else {
                console.log(sb, bitMask);
                this.appendStringRepresentationOfBitMaskToStringBuilder(sb, bitMask, nameFetcher, separator);
            }
        }

        var NetworkInterface = Java.use("java.net.NetworkInterface")
        NetworkInterface.getAll.implementation = function () {
            var nis = this.getAll()
            console.log("call getAll =========!!!")
            nis.forEach(function (ni) {
                if (ni.name.value.indexOf("tun0") >= 0 || ni.name.value.indexOf("ppp0") >= 0) {
                    ni.name.value = "xxxx"
                    ni.displayName.value = "xxxx"
                }
            })
            return nis
        }

        NetworkInterface.getName.implementation = function () {
            var name = this.getName();
            console.log("name: " + name);
            if (name == "tun0" || name == "ppp0") {
                return "rmnet_data0";
            } else {
                return name;
            }
        }


    })
}


export { passVpn }