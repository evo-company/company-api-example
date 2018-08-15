'use strict';

const https = require('https');

const AUTH_TOKEN = ''  // Your authorization token
const HOST = 'my.prom.ua' // e.g.: my.prom.ua, my.tiu.ru, my.satu.kz, my.deal.by, my.prom.md
const PORT = 443;


class EvoExampleAPI {

    constructor(token) {
        this.token = token;
    }

    makeApiCall(method, url, body) {

        return new Promise((resolve, reject) => {

            let options = {
                hostname: HOST,
                port: PORT,
                path: url,
                method: method,
                headers: {
                    "Authorization": `Bearer ${this.token}`,
                    "Content-Type": "application/json"
                }
            };

            console.log('options:', options);

            if (body) {
                body = JSON.stringify(body);
                options['headers']['Content-Length'] = body.length;
            } else {
                body = '';
            }

            var req = https.request(options, (res) => {
                let result = '';

                res.setEncoding('utf8');

                res.on('data', (chunk) => {
                    result += chunk;
                });

                res.on('end', () => {
                    resolve(JSON.parse(result));
                });
            });

            req.on('error', (e) => {
                reject(e);
            });

            req.write(body);
            req.end();
        });
    }

    getOrderList() {
        const method = 'GET';
        const url = '/api/v1/orders/list';

        return this.makeApiCall(method, url, null);
    }

    getOrderById(id) {
        const method = 'GET';
        const url = `/api/v1/orders/${id}`;

        return this.makeApiCall(method, url, null);
    }

    setOrderStatus(ids, status, cancellationReason, cancellationText) {
        const method = 'POST';
        const url = '/api/v1/orders/set_status';
        let body = {
            ids: ids,
            status: status
        };

        if (cancellationReason) {
            body['cancellation_reason'] = cancellationReason;
        }

        if (cancellationText) {
            body['cancellation_text'] = cancellationText;
        }

        return this.makeApiCall(method, url, body);
    }

}


// Usage example:

if (!AUTH_TOKEN)
    throw new Error('Sorry, there\' no any AUTH_TOKEN!');

let client = new EvoExampleAPI(AUTH_TOKEN);
let orderId;

let getOrderId = client.getOrderList().then(
    (orderList) => {
        if (!orderList['orders'])
            throw new Error('Sorry, there\'s no any order!');

        orderId = orderList['orders'][0]['id'];
        return orderId;
    },
    (error) => {
        console.error(error);
    }
);

let setOrderStatus = getOrderId.then(
    (orderId) => {
        let status = 'delivered';
        return client.setOrderStatus([orderId], status);
    },
    (error) => {
        console.error(error);
    }
);

// Getting info whether status is success
let order = setOrderStatus.then(
    (response) => {
        console.log(response);
        return client.getOrderById(orderId);
    },
    (error) => {
        console.error(error);
    }
).then(
    (response) => {
        console.log(response);
    },
    (error) => {
        console.error(error);
    }
);


module.exports = new EvoExampleAPI(AUTH_TOKEN);
