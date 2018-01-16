<?php
define('AUTH_TOKEN', '');  // Your authorization token
define('HOST', 'my.prom.ua');  // e.g.: my.prom.ua, my.tiu.ru, my.satu.kz, my.deal.by, my.prom.md


class EvoExampleClient {

    function EvoExampleClient($token) {
        $this->token = $token;
    }

    function make_request($method, $url, $body) {
        $headers = array (
            'Authorization: Bearer ' . $this->token,
            'Content-Type: application/json'
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'https://' . HOST . $url);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        if (strtoupper($method) == 'POST') {
            curl_setopt($ch, CURLOPT_POST, true);
        }

        if (!empty($body)) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($body));
        }

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $result = curl_exec($ch);
        curl_close($ch);

        return json_decode($result, true);
    }

    /**
     * Получить список заказов
     * @param string $status Возможные статусы заказов: pending - вновь созданный; received - принят в обработку; canceled - отменен
     * @return array
     */
    function get_order_list($status = NULL) {
        $url = '/api/v1/orders/list';
		if ( !is_null($status) )
		{
			$url .= '?'.http_build_query(array('status'=>$status));
		}        
        $method = 'GET';

        $response = $this->make_request($method, $url, NULL);

        return $response;
    }

    function get_order_by_id($id) {
        $url = '/api/v1/orders/' . $id;
        $method = 'GET';

        $response = $this->make_request($method, $url, NULL);

        return $response;
    }
	
    /**
     * Изменять статус заказа.
     * @param array $ids Массив номеров заказов
     * @param string $status Статус [ pending, received, delivered, canceled, draft, paid ]
     * @param string $cancellation_reason Только для статуса canceled [ not_available, price_changed, buyers_request, not_enough_fields, duplicate, invalid_phone_number, less_than_minimal_price, another ]
     * @param string $cancellation_text Толкьо для причины отмены "price_changed", "not_enough_fields" или "another"
     * @return array
     */
    function set_order_status($ids, $status, $cancellation_reason = NULL, $cancellation_text = NULL) {
        $url = '/api/v1/orders/set_status';
        $method = 'POST';

        $body = array (
             'status'=> $status,
             'ids'=> $ids
        );
	if ( $status === 'canceled' )
	{
		$body['cancellation_reason'] = $cancellation_reason;

		if ( in_array($cancellation_reason,array('price_changed', 'not_enough_fields', 'another')) )
			$body['cancellation_text'] = $cancellation_text;
	}
	    

        $response = $this->make_request($method, $url, $body);

        return $response;
    }
}


if (empty(AUTH_TOKEN)) {
    throw new Exception('Sorry, there\'s no any AUTH_TOKEN');
}

$client = new EvoExampleClient(AUTH_TOKEN);

$order_list = $client->get_order_list();
if (empty($order_list['orders'])) {
    throw new Exception('Sorry, there\'s no any order');
}
// echo var_dump($order_list);

$order_id = $order_list['orders'][0]['id'];

$order = $client->get_order_by_id($order_id);
// echo var_dump($order);

$set_status_result = $client->set_order_status((array) $order_id, 'received', NULL, NULL);
// echo var_dump($set_status_result);

$order = $client->get_order_by_id($order_id);
// echo var_dump($order);

?>
