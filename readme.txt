jdk path
/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/bin

keytool -genkeypair -keyalg RSA -keysize 2048 -sigalg SHA1withRSA -validity 36000 -alias www.zlex.org -keystore ~/zlex.keystore -dname "CN=www.zlex.org, OU=zlex, O=zlex, L=BJ, ST=BJ, C=CN"

keytool -exportcert -alias www.zlex.org -keystore ~/zlex.keystore -file ~/zlex.cer -rfc —storepass 123456





【题目描述】
某行为了发展互联网金融，准备对外开发一些接口，现请您为其设计一个统一通用入口。
【试题要求】
1. 请分别为客户端、服务端生成文件证书
2. 请使用数字信封保护报文中的敏感数据
3. 请使用数据签名确保数据的不可否认性
4. 最终程序应该提供可执行的脚本或jar包，并附上可以运行的说明
5. 最终程序导出为war包的，验证服务器可以提供tomcat8.0.24供部署，同样需要部署说明
【补充说明】
	通讯使用HTTP协议，数据报文为JSON格式，采用UTF-8编码，使用POST方式提交；文件证书密钥长度为2048位，使用RSA算法，有效期得不低于24月；数字信封中的对称加密算法使用AES，密钥长度为128位，每次请求密钥不能相同，要保证一次一密；所有密文和签名摘要统一采用Base64编码。服务器端根据clientId加载文件证书（公钥），在body中必须有一个或多个加密字段。
【输入报文】

  {
"request": {
	"header": {
		"serviceId": "接口编号",
		"clientId": "客户端编号"
	},
	"body": {
		/* 交易数据 */
	},
	"secret": "密钥密文"
},
"sign": "数字签名"
}

【输出报文】

  {
"response": {
	"header": {
		"retCode": "处理结果",
		"retMsg": "处理消息"
	},
	"body": {
		/* 交易数据 */
	}
},
"sign": "签名摘要"
}


【输入报文样例】

  {"request":{"header":{"clientId":"C001","serviceId":"F001"},"secret":"VrY7Z4sHBJvQNL1tfVUsJpR/av323gACY4Av8/XiHdg0MGSHg7Coob5H8ht7ktwmbvhX1tyj10hlq1FFT5k+btUK5jjV/yGc4epDHiHK1VhsatXoAOF1InEL0sLDk7ym5MLobnVp2gVeqhUy9CoFk2i6vvhJk4mdT/pTXjmuhqhkjdYyyhA6HQd9V07FTeWQzSdbQxTsMrbimAOuXj6r90udkhwjIXK7U3ihxz0yDuyntrfqNVmWNNctVzi6AjpK7M6AzTDd91UrrT5PagxXJbO1hAURoz/fw3tuZoGAZCUBJtdVl/IsdpkIAvTQOoZY10+TGz87YrW0ezMRyLvIYA==","body":{"password":"Xtd4/tOwO32343WhmTjOsg=="}},"sign":"KWBqHKWAUfag0LlzKLtkptBd3pEhLwyraPGEKEEFtvZsOMPu6sOuY1MVqip216z0bctnDK/LfF5A2l/TOthsztRF8KXUfwn6cikTjGRFMdKRdVxk2E7ggHbwEa9lHwRhN+I7dXhJSHomhewj+3eFKX1PtLlLlAho/kCHQuZxJ1K0HTaMYwuWJWmaZ6QkEyL2e5oeb9awbYrK/8euGQAkyp2Kc4k7RbTgTfPOo93cqNN8HN7TUf+GsO9sWrelKrLM1655tax2ZPzA0QgPMZv0SfzsEdGePiYv7N2IMdCAXOj5mIw49bZJChTrTxu8VQmr40iGL45A0ZTfl+DGrdpn7A=="}


【输出报文样例】

  {"response":{"header":{"retCode":"0000","retMsg":""},"body":{"result":"交易成功."}},"sign":"U/sNiRm1MUmQhsnioekFFGZA4q6n4HGJlphMUIeXl+kQRCw3wE2GfZ6tqBeZ5LcQBtSDHkvkEOaDR7IoBFbngNd9YlA6D4zAE8mKvTO0kjDhR1jNMc+7QeUPSs9BnN7HTI42MvzcpZ8CVBWSNyaFah7Tbz3PdNwiHMsDF8f190KXZqDdNt3oiYApu7tbRfUw5GnBTankARh03w1TftRuOr2fPyUicE51TEfub3/fv0ZJ4QjcqrPaV9xE9H7fr3+6V8cpqO8i9ug3wNKaOmHPenfJ1ytymqwVdDOZx4pjDQ07RL5J+/Cw+KkMVqernUD9HdwePjggRwqW58RD+92DDg=="}


【基线时间】
1小时
【评分标准】
1） 数据信封、签名验证正确实现得分50%
2） 全部实现得满分