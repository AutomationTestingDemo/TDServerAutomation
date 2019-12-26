pipeline{
	agent {
		label 'master'
	}
	environment {
		mvnHome = tool name: 'mvn3', type: 'maven'
		mvnCmd = "${mvnHome}/bin/mvn"	
		gitBranch = sh(returnStdout: true, script: 'echo ${GIT_BRANCH#*/}')		
		Mailto = 'payment-security-team-mra.pdl@broadcom.com'
		suitFile="xmlfiles/3DS_AllFlows_${gitBranch}.xml"
	}
	stages{	 
			stage('Automation mvn build'){
			  steps{
				sh label: '', script: 'chmod 755 envscripts/"${GIT_BRANCH#*/}.sh"'
				sh label: '', script: './envscripts/"${GIT_BRANCH#*/}.sh"'
				sh label: '', script: "${mvnCmd} clean test -DsuiteXmlFile=${suitFile}" 
				}									
		}
	}
	post{
	  always {
			echo 'Deleting the workspace'
			deleteDir()
	  }
	  success{
		echo 'Automation result are publishing report to httpd'	  
		sh label: '', script: "mkdir -p /var/www/html/${gitBranch}"
		sh label: '', script: "mv TestResultReport/3DSAutomationTestReport.html /var/www/html/${gitBranch}"
		sh label: '', script: "sudo chown -R $USER:$USER /var/www/html/${gitBranch}"
		sh label: '', script: "sudo chmod -R 755 /var/www"
		sendEmailNotification()
		sh label: '', script: "exit 0"
	  }
	  failure{
		echo ''Automation result are publishing report to httpd'		  
		sh label: '', script: "mkdir -p /var/www/html/${gitBranch}"
		sh label: '', script: "mv TestResultReport/3DSAutomationTestReport.html /var/www/html/${gitBranch}"
		sh label: '', script: "sudo chown -R $USER:$USER /var/www/html/${gitBranch}"
		sh label: '', script: "sudo chmod -R 755 /var/www"
		sendEmailNotification()
		sh label: '', script: "exit 1"	
	  }
	}
 }
def sendEmailNotification(){
	emailext mimeType: 'text/html',	
	body: '''Hi All,
	<p><strong><u>Automation Summary</u></strong></p>
	<p style="padding-right: 5px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<strong>Git Branch : '''+ gitBranch.trim() +'''</strong></p>
	<p style="padding-right: 5px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<strong>Automation Test Report :</strong><u> http://10.80.89.200:80/'''+ gitBranch.trim() +'''/3DSAutomationTestReport.html</u>.</p>
	<p style="padding-right: 50px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<b><i>${BUILD_LOG_REGEX, regex="^API URL", linesBefore=0, linesAfter=0, maxMatches=1, showTruncatedLines=false}</i></b>.</p>	
	--<br/>
	<strong>Following is the last 50 lines of the log</strong>.<br/>
	<br/>
	--LOG-BEGIN--<br/>
	<pre style=\'line-height: 22px; display: block; color: #333; font-family: Monaco,Menlo,Consolas,"Courier New",monospace; padding: 10.5px; margin: 0 0 11px; font-size: 13px; word-break: break-all; word-wrap: break-word; white-space: pre-wrap; background-color: #f5f5f5; border: 1px solid #ccc; border: 1px solid rgba(0,0,0,.15); -webkit-border-radius: 4px; -moz-border-radius: 4px; border-radius: 4px;\'>
	${BUILD_LOG, maxLines=50, escapeHtml=true}
	</pre>
	--LOG-END--''',  to: Mailto , subject:gitBranch+' branch - Automation Test Summary - ${BUILD_TIMESTAMP}'
}