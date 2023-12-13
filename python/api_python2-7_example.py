import json
import pprint
import urllib2


AUTH_TOKEN = ''  # Your authorization token
HOST = 'my.prom.ua'  # e.g.: my.prom.ua, my.satu.kz, my.prom.md


class HTTPError(Exception):
    pass


class EvoClientExample(object):

    def __init__(self, token):
        self.token = token

    def make_request(self, url, body=None):
        headers = {'Authorization': 'Bearer {}'.format(self.token),
                   'Content-Type': 'application/json'}
        if body:
            body = json.dumps(body)

        request = urllib2.Request(
            url='https://{}{}'.format(HOST, url),
            data=body,
            headers=headers
        )

        response = urllib2.urlopen(request)
        if response.code != 200:
            raise HTTPError('{}: {}'.format(response.status, response.reason))

        response_data = response.read()
        return json.loads(response_data.decode())

    def get_order_list(self):
        url = '/api/v1/orders/list'

        return self.make_request(url)

    def get_order(self, order_id):
        url = '/api/v1/orders/{id}'

        return self.make_request(url.format(id=order_id))

    def set_order_status(self, status, ids, cancellation_reason=None, cancellation_text=None):
        url = '/api/v1/orders/set_status'

        body = {
            'status': status,
            'ids': ids
        }
        if cancellation_reason:
            body['cancellation_reason'] = cancellation_reason

        if cancellation_text:
            body['cancellation_text'] = cancellation_text

        return self.make_request(url, body)


def main():
    # Initialize Client
    if not AUTH_TOKEN:
        raise Exception('Sorry, there\'s no any AUTH_TOKEN!')

    api_example = EvoClientExample(AUTH_TOKEN)

    order_list = api_example.get_order_list()
    if not order_list['orders']:
        raise Exception('Sorry, there\'s no any order!')

    pprint.pprint(api_example.get_order_list())

    # Order example data. Requred to be setup to get example work
    order_id = order_list['orders'][0]['id']
    order_ids = [order_id]
    status = 'received'

    # Setting order status
    pprint.pprint(api_example.set_order_status(status=status, ids=order_ids))

    # # Getting order by id
    pprint.pprint(api_example.get_order(order_id))


if __name__ == '__main__':
    main()
