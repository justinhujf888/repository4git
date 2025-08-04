import CryptoJS from 'crypto-es'
// import CryptoJS from 'CryptoJS';
import {Config} from '@/api/config.js';
// const a = require()
var keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
//将Ansi编码的字符串进行Base64编码
function encode64(input) {
	var output = "";
	var chr1, chr2, chr3 = "";
	var enc1, enc2, enc3, enc4 = "";
	var i = 0;
	do {
		chr1 = input.charCodeAt(i++);
		chr2 = input.charCodeAt(i++);
		chr3 = input.charCodeAt(i++);
		enc1 = chr1 >> 2;
		enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
		enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
		enc4 = chr3 & 63;
		if (isNaN(chr2)) {
			enc3 = enc4 = 64;
		} else if (isNaN(chr3)) {
			enc4 = 64;
		}
		output = output + keyStr.charAt(enc1) + keyStr.charAt(enc2) +
			keyStr.charAt(enc3) + keyStr.charAt(enc4);
		chr1 = chr2 = chr3 = "";
		enc1 = enc2 = enc3 = enc4 = "";
	} while (i < input.length);
	return output;
}
//将Base64编码字符串转换成Ansi编码的字符串
function decode64(input) {
	var output = "";
	var chr1, chr2, chr3 = "";
	var enc1, enc2, enc3, enc4 = "";
	var i = 0;


	if (input.length % 4 != 0) {
		return "";
	}
	var base64test = /[^A-Za-z0-9\+\/\=]/g;
	if (base64test.exec(input)) {
		return "";
	}
	do {
		enc1 = keyStr.indexOf(input.charAt(i++));
		enc2 = keyStr.indexOf(input.charAt(i++));
		enc3 = keyStr.indexOf(input.charAt(i++));
		enc4 = keyStr.indexOf(input.charAt(i++));
		chr1 = (enc1 << 2) | (enc2 >> 4);
		chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
		chr3 = ((enc3 & 3) << 6) | enc4;


		output = output + String.fromCharCode(chr1);
		if (enc3 != 64) {
			output += String.fromCharCode(chr2);
		}
		if (enc4 != 64) {
			output += String.fromCharCode(chr3);
		}
		chr1 = chr2 = chr3 = "";
		enc1 = enc2 = enc3 = enc4 = "";
	} while (i < input.length);
	return output;
}


function utf16to8(str) {
	var out, i, len, c;


	out = "";
	len = str.length;
	for (i = 0; i < len; i++) {
		c = str.charCodeAt(i);
		if ((c >= 0x0001) && (c <= 0x007F)) {
			out += str.charAt(i);
		} else if (c > 0x07FF) {
			out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F));
			out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F));
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
		} else {
			out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F));
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F));
		}
	}
	return out;
}


function utf8to16(str) {
	var out, i, len, c;
	var char2, char3;


	out = "";
	len = str.length;
	i = 0;
	while (i < len) {
		c = str.charCodeAt(i++);
		switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				// 0xxxxxxx
				out += str.charAt(i - 1);
				break;
			case 12:
			case 13:
				// 110x xxxx   10xx xxxx
				char2 = str.charCodeAt(i++);
				out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
				break;
			case 14:
				// 1110 xxxx  10xx xxxx  10xx xxxx
				char2 = str.charCodeAt(i++);
				char3 = str.charCodeAt(i++);
				out += String.fromCharCode(((c & 0x0F) << 12) |
					((char2 & 0x3F) << 6) |
					((char3 & 0x3F) << 0));
				break;
		}
	}
	return out;
}


function forMatNum(num) {
	return num < 10 ? '0' + num : num + '';
}

function initDays(year, month) {
	let totalDays = new Date(year, month, 0).getDate();
	let dates = [];
	for (let d = 1; d <= totalDays; d++) {
		dates.push(forMatNum(d));
	};
	return dates;
}

function initPicker(start, end, mode = "date", step = 1) {
	let initstartDate = new Date(start);
	let endDate = new Date(end);
	let startYear = initstartDate.getFullYear();
	let startMonth = initstartDate.getMonth();
	let endYear = endDate.getFullYear();
	let years = [],
		months = [],
		days = [],
		hours = [],
		minutes = [];
	let totalDays = new Date(startYear, startMonth, 0).getDate();
	for (let s = startYear; s <= endYear; s++) {
		years.push(s + '');
	};
	for (let m = 1; m <= 12; m++) {
		months.push(forMatNum(m));
	};
	for (let d = 1; d <= totalDays; d++) {
		days.push(forMatNum(d));
	}
	for (let h = 0; h < 24; h++) {
		hours.push(forMatNum(h));
	}
	for (let m = 0; m < 60; m += step) {
		minutes.push(forMatNum(m));
	}
	if (mode == "date") {
		return {
			years,
			months,
			days
		}
	} else if (mode == "dateTime") {
		return {
			years,
			months,
			days,
			hours,
			minutes
		}
	} else if (mode == "time") {
		return {
			hours,
			minutes
		}
	}
}

function encryptByDES(message, key) {
	// For the key, when you pass a string,
	// it's treated as a passphrase and used to derive an actual key and IV.
	// Or you can pass a WordArray that represents the actual key.
	// If you pass the actual key, you must also pass the actual IV.
	var keyHex = CryptoJS.enc.Utf8.parse(key);
	// console.log(CryptoJS.enc.Utf8.stringify(keyHex), CryptoJS.enc.Hex.stringify(keyHex));
	// console.log(CryptoJS.enc.Hex.parse(CryptoJS.enc.Utf8.parse(key).toString(CryptoJS.enc.Hex)));
	// CryptoJS use CBC as the default mode, and Pkcs7 as the default padding scheme
	var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
		mode: CryptoJS.mode.ECB,
		padding: CryptoJS.pad.Pkcs7
	});
	// decrypt encrypt result
	// var decrypted = CryptoJS.DES.decrypt(encrypted, keyHex, {
	//     mode: CryptoJS.mode.ECB,
	//     padding: CryptoJS.pad.Pkcs7
	// });
	// console.log(decrypted.toString(CryptoJS.enc.Utf8));
	// when mode is CryptoJS.mode.CBC (default mode), you must set iv param
	// var iv = 'inputvec';
	// var ivHex = CryptoJS.enc.Hex.parse(CryptoJS.enc.Utf8.parse(iv).toString(CryptoJS.enc.Hex));
	// var encrypted = CryptoJS.DES.encrypt(message, keyHex, { iv: ivHex, mode: CryptoJS.mode.CBC });
	// var decrypted = CryptoJS.DES.decrypt(encrypted, keyHex, { iv: ivHex, mode: CryptoJS.mode.CBC });
	// console.log('encrypted.toString()  -> base64(ciphertext)  :', encrypted.toString());
	// console.log('base64(ciphertext)    <- encrypted.toString():', encrypted.ciphertext.toString(CryptoJS.enc.Base64));
	// console.log('ciphertext.toString() -> ciphertext hex      :', encrypted.ciphertext.toString());
	return encrypted.toString();
}

function decryptByDES(ciphertext, key) {
	var keyHex = CryptoJS.enc.Utf8.parse(key);
	// direct decrypt ciphertext
	var decrypted = CryptoJS.DES.decrypt({
		ciphertext: CryptoJS.enc.Base64.parse(ciphertext)
	}, keyHex, {
		mode: CryptoJS.mode.ECB,
		padding: CryptoJS.pad.Pkcs7
	});
	return decrypted.toString(CryptoJS.enc.Utf8);
}

// 中文标点转英文标点
function exchangeChn2En(s) {
	/*正则转换中文标点*/
	s=s.replace(/：/g,':');
	s=s.replace(/。/g,'.');
	s=s.replace(/“/g,'"');
	s=s.replace(/”/g,'"');
	s=s.replace(/【/g,'[');
	s=s.replace(/】/g,']');
	s=s.replace(/《/g,'<');
	s=s.replace(/》/g,'>');
	s=s.replace(/，/g,',');
	s=s.replace(/？/g,'?');
	s=s.replace(/、/g,',');
	s=s.replace(/；/g,';');
	s=s.replace(/（/g,'(');
	s=s.replace(/）/g,')');
	s=s.replace(/‘/g,"'");
	s=s.replace(/’/g,"'");
	s=s.replace(/『/g,"[");
	s=s.replace(/』/g,"]");
	s=s.replace(/「/g,"[");
	s=s.replace(/」/g,"]");
	s=s.replace(/﹃/g,"[");
	s=s.replace(/﹄/g,"]");
	s=s.replace(/〔/g,"{");
	s=s.replace(/〕/g,"}");
	s=s.replace(/—/g,"-");
	s=s.replace(/·/g,".");
	s=s.replace(/ /g," ");
	/*正则转换全角为半角*/
	//字符串先转化成数组
	s=s.split("");
	for(var i=0;i<s.length;i++){
		//全角空格处理
		if(s[i].charCodeAt(0)===12288){
		   s[i]=String.fromCharCode(32);
		}
		/*其他全角*/
		if(s[i].charCodeAt(0)>0xFF00 && s[i].charCodeAt(0)<0xFFEF){
		   s[i]=String.fromCharCode(s[i].charCodeAt(0)-65248);
		}
	}
	//数组转换成字符串
	return s.join("");
}

// 英文符号转中文
function exchangeEn2Chn(s)
{
	let tmp = '';
	for(let i=0;i<s.length;i++)
	{
	  tmp += String.fromCharCode(s.charCodeAt(i)+65248)
	}
	return tmp;
}

function swap(array, first, second) {
    let tmp = array[second];
    array[second] = array[first];
    array[first] = tmp;
    return array;
}

/**
 * floatObj 包含加减乘除四个方法，能确保浮点数运算不丢失精度
 *
 * 我们知道计算机编程语言里浮点数计算会存在精度丢失问题（或称舍入误差），其根本原因是二进制和实现位数限制有些数无法有限表示
 * 以下是十进制小数对应的二进制表示
 *      0.1 >> 0.0001 1001 1001 1001…（1001无限循环）
 *      0.2 >> 0.0011 0011 0011 0011…（0011无限循环）
 * 计算机里每种数据类型的存储是一个有限宽度，比如 JavaScript 使用 64 位存储数字类型，因此超出的会舍去。舍去的部分就是精度丢失的部分。
 *
 * ** method **
 *  add / subtract / multiply /divide
 *
 * ** explame **
 *  0.1 + 0.2 == 0.30000000000000004 （多了 0.00000000000004）
 *  0.2 + 0.4 == 0.6000000000000001  （多了 0.0000000000001）
 *  19.9 * 100 == 1989.9999999999998 （少了 0.0000000000002）
 *
 * floatObj.add(0.1, 0.2) >> 0.3
 * floatObj.multiply(19.9, 100) >> 1990
 *
 */
var floatObj = function() {

    /*
     * 判断obj是否为一个整数
     */
    function isInteger(obj) {
        return Math.floor(obj) === obj
    }

    /*
     * 将一个浮点数转成整数，返回整数和倍数。如 3.14 >> 314，倍数是 100
     * @param floatNum {number} 小数
     * @return {object}
     *   {times:100, num: 314}
     */
    function toInteger(floatNum) {
        var ret = {times: 1, num: 0}
        if (isInteger(floatNum)) {
            ret.num = floatNum
            return ret
        }
        var strfi  = floatNum + ''
        var dotPos = strfi.indexOf('.')
        var len    = strfi.substr(dotPos+1).length
        var times  = Math.pow(10, len)
        var intNum = parseInt(floatNum * times + 0.5, 10)
        ret.times  = times
        ret.num    = intNum
        return ret
    }

    /*
     * 核心方法，实现加减乘除运算，确保不丢失精度
     * 思路：把小数放大为整数（乘），进行算术运算，再缩小为小数（除）
     *
     * @param a {number} 运算数1
     * @param b {number} 运算数2
     * @param digits {number} 精度，保留的小数点数，比如 2, 即保留为两位小数
     * @param op {string} 运算类型，有加减乘除（add/subtract/multiply/divide）
     *
     */
    function operation(a, b, digits, op) {
        var o1 = toInteger(a)
        var o2 = toInteger(b)
        var n1 = o1.num
        var n2 = o2.num
        var t1 = o1.times
        var t2 = o2.times
        var max = t1 > t2 ? t1 : t2
        var result = null
        switch (op) {
            case 'add':
                if (t1 === t2) { // 两个小数位数相同
                    result = Math.floor((n1 + n2) * Math.pow(10,digits)) / Math.pow(10,digits);
                } else if (t1 > t2) { // o1 小数位 大于 o2
                    result = Math.floor((n1 + n2 * (t1 / t2)) * Math.pow(10,digits)) / Math.pow(10,digits);
                } else { // o1 小数位 小于 o2
                    result = Math.floor((n1 * (t2 / t1) + n2) * Math.pow(10,digits)) / Math.pow(10,digits);
                }
                return Math.floor((result / max) * Math.pow(10,digits)) / Math.pow(10,digits);
            case 'subtract':
                if (t1 === t2) {
                    result = Math.floor((n1 - n2) * Math.pow(10,digits)) / Math.pow(10,digits);
                } else if (t1 > t2) {
                    result = Math.floor((n1 - n2 * (t1 / t2)) * Math.pow(10,digits)) / Math.pow(10,digits);
                } else {
                    result = Math.floor((n1 * (t2 / t1) - n2) * Math.pow(10,digits)) / Math.pow(10,digits);
                }
                return result / max;
            case 'multiply':
                result = Math.floor(((n1 * n2) / (t1 * t2)) * Math.pow(10,digits)) / Math.pow(10,digits);
                return result;
            case 'divide':
                result = Math.floor(((n1 / n2) * (t2 / t1)) * Math.pow(10,digits)) / Math.pow(10,digits);
                return result;
        }
    }

    // 加减乘除的四个接口
    function add(a, b, digits) {
        // return operation(a, b, digits, 'add');
		return toParseFloatFixed(a+b,digits);
    }
	// 减法
    function subtract(a, b, digits) {
        // return operation(a, b, digits, 'subtract');
		return toParseFloatFixed(a-b,digits);
    }
	// 乘法
    function multiply(a, b, digits) {
        // return operation(a, b, digits, 'multiply');
		return toParseFloatFixed(a*b,digits);
    }
	// 除法
    function divide(a, b, digits) {
        // return operation(a, b, digits, 'divide');
		return toParseFloatFixed(a/b,digits);
    }
    // toFixed 修复
    function toFixed(num, s) {
        var times = Math.pow(10, s)
        var des = num * times + 0.5
        des = parseInt(des, 10) / times
        return des + ''
    }
	function toParseFloatFixed(num,s) {
		return parseFloat(toFixed(num,s));
	}
    // exports
    return {
        add: add,
        subtract: subtract,
        multiply: multiply,
        divide: divide,
		toFixed: toFixed,
		toParseFloatFixed: toParseFloatFixed
    }
}();

function isJson(obj) {
		return typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length;
}

function callPhone(phone) {
	uni.makePhoneCall({
	    phoneNumber: phone
	});
}

export default {
	forMatNum,
	initDays,
	initPicker,
	encode64,
	decode64,
	utf16to8,
	utf8to16,
	base64EncodeString(str) {
		return encode64(utf16to8(str));
	},
	base64DecodeString(str) {
		return utf8to16(decode64(str));
	},
	random_string(len) {
		len = len || 32;
		var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
		var maxPos = chars.length;
		var pwd = '';
		for (let i = 0; i < len; i++) {
			pwd += chars.charAt(Math.floor(Math.random() * maxPos));
		}
		return pwd;
	},
	encryptByDES,
	decryptByDES,
	encryptStoreInfo(message) {
		return encryptByDES(message, Config.desKey);
	},
	decryptStoreInfo(message) {
		return decryptByDES(message, Config.desKey);
	},
	intoStorgeCry(key,message) {
		return uni.setStorageSync(key,encryptByDES(message, Config.desKey));
	},
	giveStorgeCry(key) {
		return decryptByDES(uni.getStorageSync(key), config.desKey);
	},
	buildPasswordStr(v) {
		return v.substr(6, 23);
	},
	exchangeChn2En,
	exchangeEn2Chn,
	swap,
	floatObj,
	isJson,
	callPhone
}
