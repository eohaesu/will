
const call = {

	/**
	 *	상담원 연결 시간 체크
	 */
	counselorCheckTime: (csst, csed) => {
		return axios.post(contextPath + "/common/ajax/checkWorkTime", { cs_time_st: csst, cs_time_ed: csed });
	},

	/**
	 *	상담사 연결
	 *  
	 */
	connectCounselor: (counselorCode, call_end) => {
		axios.post(contextPath + "/api/wms/external/callTransfer", { counselorCode: counselorCode }).then(({ data }) => {
			if (data && call_end) {
				location.replace(contextPath + "/common/sessionEndWithParam?link=true");
			} else {
				alert("상담사 연결을 실패하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.");
				location.replace(contextPath + "/main");
			}
		}).catch(() => {
			alert("상담사 연결을 실패하였습니다. 잠시 후 다시 시도해 주시기 바랍니다.");
			location.replace(contextPath + "/main");
		});
	},
	/**
	 *	음성 플레이
	 *  
	 */
	voicePlay: (isVoiceToggle, voiceData) => {
		let voiceArr = [];
		let voiceObject = new Object;

		if (!voiceData) {

			let type = document.querySelectorAll(".TYPE"),
				src = document.querySelectorAll(".SRC"),
				ttsClass = document.querySelectorAll(".TTSCLASS");

			type.forEach((item, index) => {
				voiceArr.push({
					"TYPE": type[index].value,
					"SRC": src[index].value,
					"TTSCLASS": ttsClass[index].value
				});
			});

		} else if (typeof voiceData === "string") {

			const voiceTemp = voiceData.split(',');

			if (voiceTemp.length > 1) {
				for (let value of voiceTemp) {
					voiceArr.push({
						"TYPE": "FILE",
						"SRC": value,
						"TTSCLASS": ""
					});
				}
			} else {
				voiceArr.push({
					"TYPE": "FILE",
					"SRC": voiceData,
					"TTSCLASS": ""
				});
			}
		} else {

			voiceArr = voiceData;
		}

		voiceObject.AUDIOSET = voiceArr;

		// 페이지마다 단순 호출일때 재생할 음성이 없으면 return
		if (!isVoiceToggle && voiceArr.length < 1) {
			return;
		}

		axios.post(contextPath + "/api/wms/external/playStart", { tocData: JSON.stringify(voiceObject), isVoiceToggle: isVoiceToggle }).then(({ data }) => {
			if (data && isVoiceToggle) {
				/* 음성 플레이 이후 UI컨트롤
				$('').show(); // 필요 시 vanilla로 수정
				$('').hide(); // 필요 시 vanilla로 수정
				*/
			}
			/*
			if (isVoiceToggle) {
				// 음소거|음재생 버튼 활성화(한번만 클릭되도록 처리)
				$('').find("button").prop("disabled", false); // 필요 시 vanilla로 수정
			}
			*/
		}).catch(() => {
			// 음성 재생 실패 시 추가 처리
		});
	},
	// isVoiceToggle : true  :: 음소거 버튼클릭
	//			     : false :: 페이지 load시 voice stop(기존 음성 stop)
	voiceStop: (isVoiceToggle, voiceData) => {

		call.voicePlay(false, voiceData);

		/* voice play 전 stop 필요 시 주석해제
		axios.post(contextPath + "/api/wms/external/playStop", { isVoiceToggle: isVoiceToggle }).then({ data } => {
			if (data && isVoiceToggle) {

			}

			call.voicePlay(false, voiceData);
			// isVoiceToggle ? $('').find("button").prop("disabled", false) : call.voicePlay(false, voiceData); // 필요 시 vanilla로 수정
		}).catch(() => {
			// 음성 중단 실패 시 추가 처리
		});
		*/

	},

	traceJourney: (traceCode) => {
		return axios.post(contextPath + "/api/wms/external/traceJourney", { traceCode: traceCode });
	},

	traceJourneyWithIvr: (traceCode) => {
		return axios.post(contextPath + "/common/ajax/traceJourneyWms", { traceCode: traceCode });
	},

	unbind: () => {
		return axios.get(contextPath + "/api/wms/external/callEnd");
	},

	connectCounselorWaitTime: (qdn) => {
		return axios.post(contextPath + "/api/wms/external/connectCounselorWaitTime", { QDN: qdn });
	},

	goToEnd: (message) => {
		return axios.post(contextPath + "/api/wms/external/goToEnd", { message: message });
	},

	sendVariable: (message) => {
		return axios.post(contextPath + "/api/wms/external/sendVariable", { message: message, type: "2" });
	},

	userDataSave: (userData, appBindDetails, timeoutSec) => {
		return axios.post(contextPath + "/api/wms/external/userDataSave", { userData: userData, appBindDetails: appBindDetails, timeoutSec: timeoutSec });
	},

	setProgress: (tp) => {
		return axios.post(contextPath + "/common/set/progress", { tp: tp });
	}

};