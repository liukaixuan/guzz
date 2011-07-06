var _IE=(document.all)?true:false;

function $(id)

{
	var v=document.getElementById(id);
	if(!v)
	{

		v=document.getElementsByName(id)[0];

	}

	return v;

}

function $import(src,path)
{
	var scripts = document.getElementsByTagName("script");
	var base=false;
	var script=null;
	var thisScript=null;

	for(var i=0; i<scripts.length; i++)
	{

		if(path.indexOf("/")!=0&&scripts[i].src)
		{
		
				if(scripts[i].src==src||scripts[i].src.indexOf("/"+src)!=-1)

				{
					path=scripts[i].src.replace(src.substring(src.lastIndexOf('/')+1),"")+path;
					thisScript=scripts[i];
					if(!_IE)
					{
						script=document.createElement("SCRIPT");

						script.src=path;
						path=script.src;
					}
				break;
			}
		}
		else if(path.indexOf("/")==0&&scripts[i].src)
		{
			var href=location.href;
			path=href.substring(0,href.lastIndexOf('/'))+path;
			thisScript=scripts[i];

			if(!_IE)
			{
				script=document.createElement("SCRIPT");

				script.src=path;

				path=script.src;
			}
			break;
		}
	
	}

	for(var i=0; i<scripts.length; i++)
	{
		if(script&&scripts[i].src==script.src)

		{
			delete script;
			return false;
		}
		if(scripts[i].src==path)return false;

	}
	if(script)
	{
		thisScript.parentNode.insertBefore(script,thisScript);
		document.write("<" + "script src='" + path + "'></" + "script>");
	}
	else if(_IE)
	{
		script=document.createElement("<" + "script src='" + path + "'></" + "script>");
		thisScript.parentNode.insertBefore(script,thisScript);
	}
	else
	{
		document.write("<" + "script src='" + path + "'></" + "script>");
	}

}

function $importCSS(href)
{
	document.write('<'+'link href='+href+' type=text/css rel=stylesheet></'+'link>');
}

function $base(src,tagName)
{
	var tags = document.getElementsByTagName(tagName);
	var base=false;
	for(var i=0; i<tags.length; i++)

	{

		if(!base&&tags[i].src)
		{
			if(tags[i].src==src||tags[i].src.indexOf("/"+src)!=-1)

			{
				base=tags[i].src.replace(src.substring(src.lastIndexOf('/')+1),"");
			}
		}
	}
	return base;

}

function registerNameSpace(np) 
{ 
    var nps=np.split('.'); 
    var nowNp="window"; 
    for(var i=0;i<nps.length;i++) 
    { 
        nowNp+="."+nps[i]; 
      if(typeof eval(nowNp)=='undefined') 
      { 
          eval(nowNp+"=new Object()"); 
      } 
    } 
}

function $ns(ns)
{
	registerNameSpace(ns);
}


function returnColor(_oObj){
	return _oObj.currentStyle ? _oObj.currentStyle['backgroundColor'] : window.getComputedStyle(_oObj, "")['backgroundColor'];
}

Array.prototype.find = function(pattern,param)
{
	param=(param)?'.'+param:'';
	for(var i=0;i<this.length;i++)
	{
		if(pattern==eval('this[i]'+param))return i;
	}
	return -1;
}

Array.prototype.contains = function(pattern,param)
{
	param=(param)?'.'+param:'';
	for(var i=0;i<this.length;i++)
	{
		if(pattern==eval('this[i]'+param))return true;
	}
	return false;
}

Array.prototype.insertAt = function(ind,value)
{
	this[this.length] = null;
	for(var i=this.length-1;i>=ind;i--)
	{
		this[i] = this[i-1];
	}
	this[ind] = value;
}
Array.prototype.remove = function(ind)
{
	for(var i=ind;i<this.length;i++)
	{
		this[i] = this[i+1]
	}
	this.length--;
}
Array.prototype.swapItems = function(ind1,ind2){
	var tmp = this[ind1];
	this[ind1] = this[ind2]
	this[ind2] = tmp;
}
Array.prototype.sort=function(up,param)
{
	param=(param)?'.'+param:'';
	for(var i=0;i<this.length;i++)
	{
		var m=eval('this[i]'+param);
		var index=i;
		for(var j=i+1;j<this.length;j++)
		{
			tmp=eval('this[j]'+param);
			if(up&&m>tmp){
				m=tmp;
				index=j;
			}
			if(!up&&m<tmp){
				m=tmp;
				index=j;
			}
		}
		if(i<index)this.swapItems(i,index);
	}
}
Array.prototype.min=function(param)
{
	if(this.length==0)return -1;
	param=(param)?'.'+param:'';
	var m=eval('this[0]'+param);
	var index=0;
	var tmp;
	for(var i=0;i<this.length;i++)
	{
		tmp=eval('this[i]'+param);
		if(m>tmp){
			m=tmp;
			index=i;
		}
	}
	return index;
}
Array.prototype.max=function(param)
{
	if(this.length==0)return -1;
	param=(param)?'.'+param:'';
	var m=eval('this[0]'+param);
	var index=0;
	var tmp;
	for(var i=0;i<this.length;i++)
	{
		tmp=eval('this[i]'+param);
		if(m<tmp){
			m=tmp;
			index=i;
		}
	}
	return index;
}

function reg2color(_sColor){
	if(_sColor.indexOf('rgb') > -1){
		aColor = _sColor.replace("(","").replace(")","").replace("rgb","").split(", ");
		aColor[0] = (Math.abs(aColor[0])).toString(16);
		aColor[0] += aColor[0].length == 1 ? "0" : "";
		aColor[1] = (Math.abs(aColor[1])).toString(16);
		aColor[1] += aColor[1].length == 1 ? "1" : "";
		aColor[2] = (Math.abs(aColor[2])).toString(16);
		aColor[2] += aColor[2].length == 1 ? "2" : "";
		return "#" + aColor.join('');
	}else{
		return _sColor;
	}
}


function insertHTML(oEdit,_sStr,hideDiv) {
	var oRng;

	oEdit=oEdit.contentWindow;
	if (_IE) {
		try
		{
			oEdit.focus();
			var selection=oEdit.document.selection;
			$log().debug("selection="+selection);
			var selectedRange;
			if(selection)
			{
				selectedRange = selection.createRange();
				selectedRange = hideDiv.range;
			}
			$log().debug("hideDiv.type="+hideDiv.type);
			$log().debug("selection.type="+selection.type);
			if(hideDiv.type.toLowerCase()=='control'||selection.type.toLowerCase()=='control')
			{
				$log().debug(0);
				var node=selectedRange.item(0);
				$log().debug("node="+node);
				var parent=node.parentNode;
				if(node.tagName.toLowerCase()=='img'&&parent.tagName.toLowerCase()=='a')
				{
					node=parent;
					parent=parent.parentNode;
				}
				$log().debug("parent="+parent);
				$log().debug("node="+node);
				node.insertAdjacentHTML('beforeBegin',_sStr);
//				var preNode=node.previousSibling;
				parent.removeChild(node);
//				$log().debug("preNode="+preNode);
//				if(!preNode)
//				{
//					parent.insertAdjacentHTML('afterBegin',_sStr);
//				}
//				else
//				{
//					try
//					{
//						preNode.insertAdjacentHTML('afterEnd',_sStr);
//					}catch(e1)
//					{
//						if(preNode.nextSibling)
//						{
//							try
//							{
//								preNode.nextSibling.insertAdjacentHTML('beforeBegin',_sStr);
//							}catch(e2)
//							{
//								try
//								{
//									var span=document.createElement('DIV');
//									alert(1);
//									span.innerHTML=_sStr;
//									alert(2);
//									alert(parent.tagName);
//									parent.appendChild(span);
//									alert(3);
//								}catch(e3)
//								{
//									alert(e3.message);
//								}
//							}
//						}
//						else
//						{
//							parent.insertAdjacentHTML('beforeEnd',_sStr);
//						}
//					}
//				}
				$log().debug(3);
				oEdit.focus();
			}
			else
			{
				$log().debug("selectedRange.text="+selectedRange.text);
				selectedRange.pasteHTML(_sStr);
				selectedRange.collapse(false);
				selectedRange.select();
				oEdit.focus();
			}
		}catch(err)
		{
			$log().error(err.message);
			$alert(err.message);
		}
	}
	else
	{
	 	oEdit.focus();

		oEdit.document.execCommand('insertHTML', false, _sStr);
	}
}

function setCopy(_sTxt)
{
	try{
		clipboardData.setData('Text',_sTxt);
	}catch(e){}
}

function $loadImg(nSrc,scale,size,other)
{
	var img=document.createElement("IMG");
	width=size;
	height=size;
	img.onerror=function()
	{
		this.error=true;
	}
//	img.onreadystatechange=complete;
	img.onload=function()
	{
		if(!this.error&&scale)
		{
			width=this.width;
			height=this.height;
			if(width>size||height>size)
			{
				if(width>height){
					height=size*height/width;
					width=size;
				}
				else
				{
					width=size*width/height;
					height=size;
				}
			}
		}
	};
	img.src=nSrc;
	if(img.error)return false;
	else
	{
		if(scale)
		{
			return '<img src="'+nSrc+'" width="'+width+'" height="'+height+'" '+other+'>';
		}
		else
		{
			return '<img src="'+nSrc+'" '+other+'>';
		}
	}
	delete img;
}

String.prototype.lTrim = function () {return this.replace(/^\s*/, "");}
String.prototype.rTrim = function () {return this.replace(/\s*$/, "");}
String.prototype.trim = function () {return this.rTrim().lTrim();}
String.prototype.endsWith = function(sEnd) {return (this.substr(this.length-sEnd.length)==sEnd);}
String.prototype.startsWith = function(sStart) {return (this.substr(0,sStart.length)==sStart);}
String.prototype.format = function(){
	var s = this; for (var i=0; i < arguments.length; i++)
	{
		s = s.replace("{" + (i) + "}", arguments[i]);
	}
	return(s);
}

if(!_IE)
{
	Object.prototype.removeNode=function(s)
	{
		if(!s||s==false)
		{
			for(var i=0;i<this.childNodes.length;i++)
			{
				this.parentNode.appendChild(this.childNodes[i]);
			}
			return this.parentNode.removeChild(this);
		}
		else
		{
			return this.parentNode.removeChild(this);
		}
	}
	HTMLElement.prototype.__defineGetter__("innerText",function()
	{
		var text=null;
		text = this.ownerDocument.createRange();
		text.selectNodeContents(this);

		text=text.toString();
		return text;
	});
};

// Create the loadXML method and xml getter for Mozilla
if ( window.DOMParser &&
	  window.XMLSerializer &&
	  window.Node && Node.prototype && Node.prototype.__defineGetter__ ) {

   if (!Document.prototype.loadXML) {
      Document.prototype.loadXML = function (s) {
         var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
         while (this.hasChildNodes())
            this.removeChild(this.lastChild);

         for (var i = 0; i < doc2.childNodes.length; i++) {
            this.appendChild(this.importNode(doc2.childNodes[i], true));
         }
      };
	}

	Document.prototype.__defineGetter__( "xml",
	   function () {
		   return (new XMLSerializer()).serializeToString(this);
	   }
	 );
}

