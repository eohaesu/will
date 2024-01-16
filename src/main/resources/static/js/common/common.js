
const common = {

	setSessionEnd: () => {
		polling.stop();
		return axios.get(contextPath + "/auth/sessionEnd");
	},

	blockHistoryBack: () => {
		window.history.pushState(null, document.title, location.href);
		window.onpopstate = () => { history.go(1); };
	},

	base64UrlEncode: (p) => {
		const ENC = { '+': '-', '/': '_' };
		return btoa(p).replace(/[+/]/g, (m) => ENC[m]);
	},

	base64UrlDecode: (p) => {
		const DEC = { '-': '+', '_': '/' };
		return atob(p).replace(/[-_]/g, (m) => DEC[m]);
	},

	chkIsJSON: (str) => {
		try {
			return (JSON.parse(str) && !!str);
		} catch (e) {
			return false;
		}
	},

	blockRefresh: () => {
		window.addEventListener('beforeunload', (event) => {
			event.preventDefault();
			event.returnValue = '';
		});
	},

	removeBlockRefresh: () => {
		window.removeEventListener('beforeunload');
	},

	replaceAt: (value, index, replacement) => {
		if (index >= value.length) {
			return value.valueOf();
		}
		return value.substring(0, index) + replacement + value.substring(index + 1);
	},

	isAndroid: () => {
		return navigator.userAgent.match(/android/i) ? true : false;
	},
	
	cellNumHyphen: (target) => {
			
		let value = target.value;
		
		if (!target.value) {
		    return "";
		  }

		value = value.replace(/[^0-9]/g, "");
		
		const result = [];
		let restNumber = "";
		
		// 시작번호 02
		if (value.startsWith("02")) {
			result.push(value.substr(0, 2));
		    restNumber = value.substring(2);
		    target.maxLength = 12;
		// 시작번호 1, 1xxx-yyyy
		}else if (value.startsWith("1")) {
			restNumber = value;
			target.maxLength = 9;
		// 나머지 3자리 지역번호, 0xx-yyyy-zzzz
		}else {
			result.push(value.substr(0, 3));
		    restNumber = value.substring(3);
		    target.maxLength = 13;
		}
		
		// 7자리만 남았을 경우, xxx-yyyy
		if (restNumber.length === 7) {
			result.push(restNumber.substring(0, 3));
		    result.push(restNumber.substring(3));
		} else {
			result.push(restNumber.substring(0, 4));
			result.push(restNumber.substring(4));
		}
		
		target.value = result.filter((val) => val).join("-");
	}
}

