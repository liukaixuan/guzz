/**
 * ViewVoteCount.java created at 2009-9-27 下午05:32:01 by liukaixuan@gmail.com
 */
package org.guzz.sample.vote.action.open;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.sample.vote.business.BigVote;
import org.guzz.sample.vote.manager.IBigVoteManager;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


/**
 sample code:<br/>
 <code>
 <pre>
 	<body>
	投票数：<span id="bigVoteCount_1">Loading</span>
	
	<script src="http://localhost:8080/vote/viewVoteCountAction.do?voteId=1"></script>
	
	<script language="javascript">
	
	document.getElementById("bigVoteCount_1").innerHTML = bigVote1['voteNum'] ;
	
	delete bigVote1 ;
	</script>
	
	</body>
 
 </pre>
 </code>
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ViewVoteCount  implements Controller{
	
	private IBigVoteManager bigVoteManager ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int voteId = RequestUtil.getParameterAsInt(request, "voteId", -1) ;

		BigVote vote = bigVoteManager.getCachedVote(voteId) ;
		
		if(vote == null){
			return null ;
		}
		
		/*
		 * 格式：
		 * <script language="javascript">

			var bigVote1 = new Array() ;
			
			bigVote1['voteCount'] = 568 ;
			bigVote1['votePeople'] = 126 ;
			
			document.getElementById("bigVoteCount_1").innerHTML = bigVote1['voteCount'] ;
			
			delete bigVote1 ;
			</script>
		 * 
		 * 
		 */
		StringBuilder sb = new StringBuilder() ;
		sb.append("\nvar bigVote").append(voteId).append(" = new Array() ;")
		  .append("\nbigVote").append(voteId).append("['voteNum'] = ").append(vote.getShowVoteNum()).append(" ;")
		  .append("\nbigVote").append(voteId).append("['votePeople'] = ").append(vote.getVotePeople()).append(" ;")
		  ;
				
		PrintWriter pw = response.getWriter() ;
		
		pw.write(sb.toString()) ;
		
		pw.close() ;
		
		return null ;
	}

	public IBigVoteManager getBigVoteManager() {
		return bigVoteManager;
	}

	public void setBigVoteManager(IBigVoteManager bigVoteManager) {
		this.bigVoteManager = bigVoteManager;
	}
	
}
