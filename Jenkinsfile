pipeline{
	agent {
		label 'master'
	}
	environment {
		mvnHome = tool name: 'mvn3', type: 'maven'
		mvnCmd = "${mvnHome}/bin/mvn"
		httpdUrl = 'http://kumaj08-i20572:81/preview/3DSAutomationTestReport.html'
		httpdServer = "http://kumaj08-i20572:81/preview/3DSAutomationTestReport.html"
		Mailto = 'payment-security-team-mra.pdl@broadcom.com'			
		gitBranch = sh(returnStdout: true, script: 'echo ${GIT_BRANCH#*/}')			
	}
	stages{	 
			stage('Automation mvn build'){
			  steps{
				sh label: '', script: "${mvnCmd} clean test -DsuitFile=xmlfiles/testfactory.xml -U" 
				}									
		}
	}
	post{
	  always {
            echo 'Automation Publish report to httpd'
            sh label: '', script: "mv *.html /var/www/html/preview/"
			echo 'Deleting the workspace'
			deleteDir()
	  }
	  success{
		echo 'Build success'
		sendEmailNotification()
		sh label: '', script: "exit 0"
	  }
	  failure{
		echo 'Build failed'
		sendEmailNotification()
		sh label: '', script: "exit 1"
	  }
	}
 }
def sendEmailNotification(){
	emailext mimeType: 'text/html',	
	body: '''Hi All,
	<p><strong><u>Automation Summary</u></strong></p>	
	<p style="padding-right: 5px;">&emsp;&emsp;&emsp;<strong>Git Branch</strong> :'''+ gitBranch +'''</p>
	<p style="padding-right: 5px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<strong>Webserver URL :</strong>  <u>'''+ httpdUrl +'''</u>.</p>
	<p style="padding-right: 50px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<b><i>${BUILD_LOG_REGEX, regex="^API URL", linesBefore=0, linesAfter=0, maxMatches=1, showTruncatedLines=false}</i></b>.</p>
	<p style="padding-right: 50px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<b><i>${BUILD_LOG_REGEX, regex="^Results", linesBefore=0, linesAfter=3, maxMatches=1, showTruncatedLines=false}</i></b>.</p>	
	--<br/>
	''',  to: Mailto , subject:gitBranch+' - Automation Summary - Build # $BUILD_NUMBER'
}
