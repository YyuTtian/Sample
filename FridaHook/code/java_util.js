
import { getCtx } from "./context.js"


let instance = null

function getJavaUtil() {
    if (instance == null) {
        // chmod 777 data/local/tmp/dex
        let dexFile = Java.openClassFile("data/local/tmp/dex")
        dexFile.load()
        let classList = dexFile.getClassNames()
    
        instance = Java.use("com.qgl.JavaUtil")
        instance.initLogFile(getCtx())
    }

    return instance
}

export { getJavaUtil }
