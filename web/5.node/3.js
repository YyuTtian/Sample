import http from 'http'

const server = http.createServer()

server.on('request', (request, response) => {

    console.log(request.method);
    console.log(request.url);

    response.statusCode = 200
    response.setHeader('Content-Type', 'text/plain; charset=utf-8')
    response.end('服务器返回hello')
})

server.listen(3000, () => {
    console.log('服务器启动了');
})