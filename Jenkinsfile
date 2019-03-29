pipeline{
		agent {
			label 'master'
		}
		environment {
			mvnHome=tool name: 'mvn3', type: 'maven'
			mvnCmd="${mvnHome}/bin/mvn"			
			Mailto = 'ajeyakumar.hulivanaboregowda@broadcom.com'			
			gitBranch = sh(returnStdout: true, script: 'echo ${GIT_BRANCH#*/}')			
		}
		stages{	 
			stage('Automation mvn build'){
			  steps{
				sh label: '', script: "${mvnCmd} clean test -DsuitFile=xmlfiles/testfactory.xml" 
				}									
		}
		stage('Publish HTML Report'){
			  steps{
				publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, includes: '**/*.html', keepAll: true, reportDir: '', reportFiles: '3DSAutomationTestReport.html', reportName: 'HTML Report', reportTitles: '']) 
				}									
		}
	}								 
post{
		always{
		  sendEmailNotification()		    
		}		
    }
 }
def sendEmailNotification(){
	emailext mimeType: 'text/html',	
	body: '''Hi All,
	<p><strong><u>Automation Summary</u></strong></p>	
	<p style="padding-right: 5px;">&emsp;&emsp;&emsp;<strong>Git Branch</strong> :'''+ gitBranch +'''</p>
	<p style="padding-right: 50px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<b><i>${BUILD_LOG_REGEX, regex="^API URL", linesBefore=0, linesAfter=0, maxMatches=1, showTruncatedLines=false}</i></b>.</p>
	<p style="padding-right: 50px;">&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;<b><i>${BUILD_LOG_REGEX, regex="^Results", linesBefore=0, linesAfter=3, maxMatches=1, showTruncatedLines=false}</i></b>.</p>	
	--<br/>
	''',  to: Mailto , subject:gitBranch+' - Automation Test Report - Build # $BUILD_NUMBER'
}
