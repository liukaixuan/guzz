
var oldLink = null;

function MyCalendar(){
	this.setActiveStyleSheet = MyCalendar_setActiveStyleSheet;
	this.selected = MyCalendar_selected;
	this.closeHandler = MyCalendar_closeHandler;
	this.showCalendar = MyCalendar_showCalendar;
	this.isDisabled = MyCalendar_isDisabled;
	this.flatSelected = MyCalendar_flatSelected;
	this.showFlatCalendar = MyCalendar_showFlatCalendar;
	this.drawCalendar = MyCalendar_drawCalendar;
	this.drawWithTime = MyCalendar_drawWithTime;
	this.drawWithoutTime = MyCalendar_drawWithoutTime;
	this.drawDialogCalendar = MyCalendar_drawDialogCalendar;
	this.openDialog = MyCalendar_openDialog;
}
MyCalendar.basePath = $base("MyCalendar.js","script");
function MyCalendar_setActiveStyleSheet(link, title) {
	var i, a, main;
	for(i=0; (a = document.getElementsByTagName("link")[i]); i++) {
	if(a.getAttribute("rel").indexOf("style") != -1 && a.getAttribute("title")) {
		a.disabled = true;
		if(a.getAttribute("title") == title) a.disabled = false;
	}
	}
	if (oldLink) oldLink.style.fontWeight = 'normal';
	oldLink = link;
	link.style.fontWeight = 'bold';
	return false;
}

// This function gets called when the end-user clicks on some date.
function MyCalendar_selected(cal, date) {
	//cal.sel.value = date;
	//if (cal.dateClicked && !bDblClickClose)
	//	cal.callCloseHandler(date);
}

function MyCalendar_closeHandler(cal) {
	cal.hide();                        // hide the calendar
//  cal.destroy();
	_dynarch_popupCalendar = null;
}

function MyCalendar_showCalendar(id, format, showsTime, showsOtherMonths) {
	var el = document.getElementById(id);
	if (_dynarch_popupCalendar != null) {
		_dynarch_popupCalendar.hide();                 // so we hide it first.
	} else {
		// first-time call, create the calendar.
		var cal = new Calendar(1, null, this.selected, this.closeHandler);
		// uncomment the following line to hide the week numbers
		// cal.weekNumbers = false;
		if (typeof showsTime == "string") {
			cal.showsTime = true;
			cal.time24 = (showsTime == "24");
		}
		if (showsOtherMonths) {
			cal.showsOtherMonths = true;
		}
		_dynarch_popupCalendar = cal;                  // remember it in the global var
		cal.setRange(1900, 2070);        // min/max year allowed.
		cal.create();
	}
	_dynarch_popupCalendar.setDateFormat(format);    // set the specified date format
	_dynarch_popupCalendar.parseDate(el.value);      // try to parse the text in field
	_dynarch_popupCalendar.sel = el;                 // inform it what input field we use

	// the reference element that we pass to showAtElement is the button that
	// triggers the calendar.  In this example we align the calendar bottom-right
	// to the button.
	_dynarch_popupCalendar.showAtElement(el.nextSibling, "Br");        // show the calendar

	return false;
}

var MINUTE = 60 * 1000;
var HOUR = 60 * MINUTE;
var DAY = 24 * HOUR;
var WEEK = 7 * DAY;

// If this handler returns true then the "date" given as
// parameter will be disabled.  In this example we enable
// only days within a range of 10 days from the current
// date.
// You can use the functions date.getFullYear() -- returns the year
// as 4 digit number, date.getMonth() -- returns the month as 0..11,
// and date.getDate() -- returns the date of the month as 1..31, to
// make heavy calculations here.  However, beware that this function
// should be very fast, as it is called for each day in a month when
// the calendar is (re)constructed.
function MyCalendar_isDisabled(date) {
	var today = new Date();
	return (Math.abs(date.getTime() - today.getTime()) / DAY) > 10;
}

function MyCalendar_flatSelected(cal, date) {
	var elPreview = document.getElementById("preview");
	var elValue = document.getElementById("DateValue");
	elPreview.innerHTML = date;
	elValue.value = date;
}

function MyCalendar_showFlatCalendar(_sDateFormat, _sInitDateString, _bShowTime) {
	var parent = document.getElementById("display");

	// construct a calendar giving only the "selected" handler.
	var cal = new Calendar(1, null, this.flatSelected, null, true);

	// hide week numbers
	cal.weekNumbers = false;

	cal.showsTime = _bShowTime;

	// We want some dates to be disabled; see function isDisabled above
	//cal.setDisabledHandler(this.isDisabled);
	cal.showsOtherMonths = true;

	var sDateFormat = _sDateFormat || "";
	if(sDateFormat != null && sDateFormat.length > 0){
		cal.setDateFormat(sDateFormat);
	} else {
		if(_bShowTime)
			cal.setDateFormat("%Y-%m-%d %H:%M");
		else
			cal.setDateFormat("%Y-%m-%d");
	}
	var sInitDateString = _sInitDateString || "";

	// this call must be the last as it might use data initialized above; if
	// we specify a parent, as opposite to the "showCalendar" function above,
	// then we create a flat calendar -- not popup.  Hidden, though, but...
	cal.create(parent);
	cal.parseDate(sInitDateString);

	// ... we can show it here.
	cal.show();
}

function MyCalendar_drawCalendar(id, sValue, format, showsTime, showsOtherMonths, bNotSubmit, sStyle, bApplyEmpty){
	var nMaxLength = 16;
	if(format == "%Y-%m-%d %H:%M") nMaxLength = 16;
	if(format == "%Y-%m-%d") nMaxLength = 10;
	//readonly
	var sHTML = "" 
			+"<input MAXLENGTH=\""+nMaxLength+"\" DateFormat=\""+format+"\""; 
			// onblur=\"checkDateString(";
	//if(bApplyEmpty) {
	//	sHTML += "true";
	//}
	sHTML += ")\"  class=\"inputtext\" type=\"text\" name=\""+id+"\" id=\""+id+"\" value=\""+sValue+"\"";
	if(sStyle != null && sStyle.length > 0){
		sHTML += " style=\""+sStyle+"\" ";
	}
	if(bNotSubmit){
		sHTML += " ignore=\"1\" ";
	}
	sHTML += "><A HREF=\"###\" onclick=\"return JSCalendar.showCalendar('"+id+"', '"+format+"',";
	if(showsTime != null && showsTime.length > 0){
		sHTML += " '"+showsTime+"', ";
	} else {
		sHTML += " null, ";
	}
	sHTML += ""+showsOtherMonths+");\"><img src=\"" + MyCalendar.basePath + "/calendar_img.gif\" style=\"cursor: pointer; border: 1px solid red;\""
			+" onmouseover=\"this.style.background='red';\" onmouseout=\"this.style.background=''\" border=\"0\"/></A>";
	document.write(sHTML);
}

function MyCalendar_drawWithTime(id, sValue, bNotSubmit, bApplyEmpty){
	if(sValue==null || sValue=="") {
		var date = new Date();
		sValue = date.getYear() + "-" + (date.getMonth()+1) + "-" + date.getDate() + " " + date.getHours() + ":" + date.getMinutes();
	}
	else {
		var arrDate = new Array();
		arrDate = sValue.split(":");
		sValue = arrDate[0] + ":" + arrDate[1];
	}
	sValue="";
	this.drawCalendar(id, sValue, '%Y-%m-%d %H:%M', '24', true, bNotSubmit, "width:110px", bApplyEmpty);
}

function MyCalendar_drawWithoutTime(id, sValue, bNotSubmit, bApplyEmpty){
	if(sValue==null || sValue=="") {
		var date = new Date();
		sValue = date.getYear() + "-" + (date.getMonth()+1) + "-" + date.getDate();
	}
	else {
		var arrDate = new Array();
		arrDate = sValue.split(" ");
		sValue = arrDate[0];
	}
	this.drawCalendar(id, sValue, '%Y-%m-%d', null, true, bNotSubmit, "width:80px", bApplyEmpty);
}

function MyCalendar_openDialog(_id, _sInitDateString, _sDateFormat, _bShowTime){
	var oEl = document.getElementById(_id);
	if(!oEl){
		alert("请确认您的id输入的是正确的！");
		return;
	}
	var args = new Array();
	args[0] = _sDateFormat || "";
	args[1] = (oEl.value)?(oEl.value||""):(_sInitDateString || "");
	args[2] = _bShowTime || false;

	//var nWidth = 206;
	var nWidth = 310;
	var nHeight = 270;
	if(args[2]) nHeight = 285;
	var oTRSAction = new CTRSAction("../wcm_use/calendar_dialog.htm");
	var sReturn = oTRSAction.doNoScrollDialogAction(nWidth, nHeight, args);
	if(!sReturn) return;
	oEl.value = sReturn;
}

function MyCalendar_drawDialogCalendar(id, _sInitDateString, _sDateFormat, _bShowTime, sStyle, bNotSubmit){
	var sFormat = _sDateFormat;
	if(!sFormat) sFormat = (_bShowTime)?"%Y-%m-%d %H:%M":"%Y-%m-%d";
	var nMaxLength = 16;
	if(sFormat == "%Y-%m-%d %H:%M") nMaxLength = 16;
	if(sFormat == "%Y-%m-%d") nMaxLength = 10;
	var sHTML = ""
			+"<input MAXLENGTH=\""+nMaxLength+"\" DateFormat=\""+sFormat+"\" class=\"inputtext\" onblur=\"checkDateString()\" type=\"text\" name=\""+id+"\" id=\""+id+"\" value=\""+_sInitDateString+"\"";
	if(sStyle != null && sStyle.length > 0){
		sHTML += " style=\""+sStyle+"\" ";
	}
	if(bNotSubmit){
		sHTML += " ignore=\"1\" ";
	}
	sHTML += "><A HREF=\"###\" onclick=\"return JSCalendar.openDialog('"+id+"', '"+_sInitDateString+"', '"+sFormat+"',";
	if(_bShowTime){
		sHTML += " "+_bShowTime;
	} else {
		sHTML += " false";
	}
	sHTML += ");\"><img src=\"../js/calendar_style/img.gif\" style=\"cursor: pointer; border: 1px solid red;\""
			+" onmouseover=\"this.style.background='red';\" onmouseout=\"this.style.background=''\" border=\"0\"/></A>";

	document.write(sHTML);
}

var JSCalendar = new MyCalendar();

// BEGIN: DATE STRING VALIDATE
function isLeapYear(year){
	if((year%4==0&&year%100!=0)||(year%400==0)){
		return true;
	}
	return false;
}

function getNumber(_str){
	var str = _str || "0";
	if(str.length > 1 && str.charAt(0) == '0'){
		return parseInt(str.substring(1));
	}
	return parseInt(str);
}

function toNumberString(_str){
	var nNumber = getNumber(_str);
	return (nNumber > 9)?(nNumber+""):("0"+nNumber);
}

function checkDateString(bApplyEmpty, ev){
	if(document.all){
		var oSrcEl = event.srcElement;
	}
	else{
		var oSrcEl = ev.currentTarget;
	}
	
	var sFormat = oSrcEl.DateFormat || "%Y-%m-%d";
	var sDateStr = oSrcEl.value || "";

	var regexp = null;
	var bHasTime = false;
	if(sFormat == "%Y-%m-%d"){
		regexp = new RegExp("^([0-9]{4})\-([0-9]{1,2})\-([0-9]{1,2})$");
		bHasTime = false;
	}else{
		if(sFormat == "%Y-%m-%d %H:%M"){
			regexp = new RegExp("^([0-9]{4})\-([0-9]{1,2})\-([0-9]{1,2}) ([0-9]{1,2}):([0-9]{1,2})$");
			bHasTime = true;
		} else {
			regexp = new RegExp("^([0-9]{4})\-([0-9]{1,2})\-([0-9]{1,2})$");
			bHasTime = false;
		}
	}
	if(bApplyEmpty && sDateStr == "") {
		return;
	}
	if(regexp.test(sDateStr)){
		var dateArray = sDateStr.match(regexp);
		var nYear = getNumber(dateArray[1]);
		var nMonth = getNumber(dateArray[2]);
		var nDay = getNumber(dateArray[3]);
		if(nYear < 1900 || nYear > 2070){
			nYear = (new Date()).getYear();
			alert("[错误] 年份请限制在 1900-2070 之间");
			oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
			return;
		}
		if(nMonth < 1 || nMonth > 12){
			nMonth = 1;
			alert("[错误] 月份请限制在 1-12 之间");
			oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
			return;
		}
		if(nDay < 1 || nDay > 31){
			nDay = 1;
			alert("[错误] 天数请限制在 1-31 之间");
			oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
			return;
		}
		if(nMonth==2){ 
			if(isLeapYear(nYear)&&nDay>29){
				nDay = 29;
				alert("闰年二月只有29天");
				oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
				return;
			}
			if(!isLeapYear(nYear)&&nDay>28){
				nDay = 28;
				alert("非闰年二月只有 28 天");
				oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
				return;
			}	
		}//end 2 month
		if((nMonth==4||nMonth==6||nMonth==9||nMonth==11)&&(nDay>30)){
			nDay = 30;
			alert(nMonth+"这个月最多只有 30 天啊");
			oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + ((bHasTime)?(" " + toNumberString(dateArray[4]) + ":" + toNumberString(dateArray[5])):"");
			return;
		}//end 30days
		if(bHasTime){
			var nHour = getNumber(dateArray[4]);
			var nMinute = getNumber(dateArray[5]);
			if(nHour < 0 || nHour > 23){
				nHour = 0;
				alert("[错误] 小时数请限制在 0-23 之间");
				oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + " " + toNumberString(nHour) + ":" + toNumberString(nMinute);
				return;
			}
			if(nMinute < 0 || nMinute > 59){
				nMinute = 0;
				alert("[错误] 分钟数请限制在 0-59 之间");
				oSrcEl.value = toNumberString(nYear) + "-" + toNumberString(nMonth) + "-" + toNumberString(nDay) + " " + toNumberString(nHour) + ":" + toNumberString(nMinute);
				return;
			}
		}
	}else{
		if(sDateStr != "") {
			alert("您输入的时间格式不正确! \n 请按照以下格式输入："+((!bHasTime)?"Year-Month-Day(As:2002-12-24)":"Year-Month-Day Hour:Minute(As:2002-12-24 11:50)"));
		}
		var date = new Date();
		oSrcEl.value = toNumberString(date.getYear()) + "-" + toNumberString(date.getMonth()) + "-" + toNumberString(date.getDate()) + ((bHasTime)?(" " + toNumberString(date.getHours()) + ":" + toNumberString(date.getMinutes())):"");
		return;
	}
}
// END: DATE STRING VALIDATE