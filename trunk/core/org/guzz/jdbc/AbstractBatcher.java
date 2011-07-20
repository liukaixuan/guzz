/**
 * 
 */
package org.guzz.jdbc;

/**
 * 
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractBatcher implements Batcher {
	
	private int batchSize ;
	
	private int defaultBatchSize ;
	
	private boolean autoExecuteUpdate = true ;
	
	protected AbstractBatcher(int defaultBatchSize){
		this.defaultBatchSize = defaultBatchSize ;
	}
	
	protected void checkAndAutoExecuteBatch(int objectsCountInBatch){
		//exceeds the batchSize
		if(autoExecuteUpdate){
			int size = batchSize > 0 ? batchSize : defaultBatchSize ;
			
			if(objectsCountInBatch >= size){
				this.executeBatch() ;
				this.clearBatch() ;
			}
		}
	}
	
	/**
	 * @deprecated
	 */
	public int[] executeUpdate(){
		return executeBatch() ;
	}
	
	public final int getBatchSize() {
		return batchSize;
	}

	public final void setBatchSize(int batchSize) {
		this.batchSize = batchSize ;
	}
	
	protected final void setDefaultBatchSize(int defaultBatchSize){
		this.defaultBatchSize = defaultBatchSize ;
	}

	public final boolean isAutoExecuteUpdate() {
		return autoExecuteUpdate;
	}

	public final void setAutoExecuteUpdate(boolean auto) {
		this.autoExecuteUpdate = auto ;
	}

}
