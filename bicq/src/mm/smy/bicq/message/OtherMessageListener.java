package mm.smy.bicq.message ;

/**
* 处理OtherMessage的接口
* 具体的OtherMessage可以继承该接口，以实现多态性处理。
*/

public interface OtherMessageListener{
	public void otherMessageAction(OtherMessage om) ;
}