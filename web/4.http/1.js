window.onload = async () => {
    console.log(`window onload`);

    // form-serialize.js 把form里的所有元素变成一个对象
    // uploadFile()

    let file = document.querySelector('#file')
    let uploadBtn = document.querySelector('#uploadBtn')
    uploadBtn.onclick = () => {
        let formData = new FormData()
        formData.append('name', '张三')
        formData.append('age', 18)

        Array.from(file.files).forEach(item => {
            formData.append('file', item)
        })

        uploadFile(formData)
    }

    let jsonResult = json()
    // let jsonResult = await axios.get('http://ajax-api.itheima.net/api/province')
    console.log(jsonResult);
    // jsonResult.then((result) => {
    //     console.log(result.data);
    // })

}

function get() {
    axios({
        url: "http://ajax-base-api-t.itheima.net/api/getbooks",
        method: "GET",
    }).then(result => {
        console.log(result.data.data);
    }).catch((e) => {
        console.log(`fail ${e}`);
    })
}

function getWithParams() {
    axios({
        url: "http://ajax-base-api-t.itheima.net/api/getbooks",
        method: "GET",
        // 参数拼接再url后面
        params: {
            id: 1,
            bookname: '西游记'
        }
    }).then(result => {
        console.log(result.data.data);
    }).catch((e) => {
        console.log(`fail ${e}`);
    })
}

function post() {
    axios({
        url: 'http://ajax-base-api-t.itheima.net/api/addbook',
        method: 'POST',
        data: {
            bookname: '123sdfasdfasdfasf',
            author: '456sadfasdfasf',
            publisher: '789asdfadf'
        }
    }).then(result => {
        console.log(result.data.data);
    }).catch((e) => {
        console.log(`fail ${e}`);
    })
}

function uploadFile(formData) {
    axios({
        url: 'http://ajax-base-api-t.itheima.net/api/file',
        method: 'POST',
        data: formData
    }).then(result => {
        console.log(result.data.data);
    }).catch((e) => {
        console.log(`fail ${e}`);
    })
}

async function json() {
    // return await axios({
    //     url: "http://ajax-api.itheima.net/api/province",
    //     method: "GET"
    // })

    // .then(result => {
    //     if (result.status == 200) {
    //         // console.log(result.data);
    //         // console.log(typeof result.data);

    //         // let jsonStr = JSON.stringify(result.data)
    //         // console.log(`对象转string ${jsonStr}`);

    //         // // string转对象
    //         // console.log(JSON.parse(jsonStr))

    //         return result.data
    //     }
    // }).catch((e) => {
    //     console.log(`fail code=${e}`);
    //     return null
    // })


    // return new Promise((resolve, reject) => {
    //     axios.get('http://ajax-api.itheima.net/api/province')
    //         .then((result) => {
    //             resolve(result.data)
    //         })
    //         .catch((e) => {
    //             reject(e)
    //         })
    // })

    return await axios.get('http://ajax-api.itheima.net/api/province')
}