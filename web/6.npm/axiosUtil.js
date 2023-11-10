import axios from "axios"

function get(url) {
    return new Promise((succ, fail) => {
        axios.get(url)
            .then((result) => {
                succ(result)
            }).catch((e) => {
                fail(e)
            })
    })
}

export default {
    get // 导出方法
}