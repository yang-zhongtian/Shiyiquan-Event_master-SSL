### 通用

请求地址: 任意页面

|     参数     |  必选  |   类型   |     说明      |
| :--------: | :--: | :----: | :---------: |
| user-agent | true | string |   appkey    |
|    sign    | true | string | 根据加密算法计算的签名 |
|            |      |        |             |

### 最近活动

请求地址: /api

|    参数    |  必选   |   类型   |         说明         |
| :------: | :---: | :----: | :----------------: |
| category | true  | string |   必选参数，填写event即可   |
|   time   | true  | string |  必选参数，填写latest即可   |
|  number  | false |  int   | 请求返回活动数量，若填写，仅接受15 |
|          |       |        |                    |

### 社团广场

请求地址: /square/club

|     参数     |  必选   |   类型   |      说明       |
| :--------: | :---: | :----: | :-----------: |
| user-agent | true  | string |    appkey     |
|    sign    | true  | string |  根据加密算法计算的签名  |
|   index    | false |  int   | 请求返回的页码，默认为1  |
|  category  | false | string | 请求的社团分类，默认all |
|            |       |        |               |

### 天台动态

请求地址: message/global

|     参数     |  必选  |   类型   |     说明      |
| :--------: | :--: | :----: | :---------: |
| user-agent | true | string |   appkey    |
|    sign    | true | string | 根据加密算法计算的签名 |
| time_last  | true | double |    时间偏移量    |
|            |      |        |             |

