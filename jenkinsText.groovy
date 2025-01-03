// defining a global variable
def gv
pipeline {
    // you can define your own variable in jenkins
    environment {
        NEW_VERSION = "1.3.4"
        // install a plugin to use credentials function
        // refernce the id of the credential you created in jenkins UI
        SERVER_CREDENTIALS = credentials('server-credentials')
    }

    agent any

    // create parameters to use later
    parameters {
        choice (name: 'VERSION', choices: ['1.1.0', '1.2.0', '1.3.0'], description: "versions to chose, to deploy on prod")
        booleanParam(name: 'executeTest', defaultValue: true, description: 'Run tests during the build')
    }

    // In a Jenkins Declarative Pipeline, the tools block is used to define tools 
    // that should be made available on the agent during the execution of the pipeline.
    // you need to install them first in jenkins UI Tools
    // tools attribute give you acess to tolls like npm, maven, gradle, JDK etc ..
    tools {
        maven 'maven-3.9' // name that i gave the instalation in Jenkins UI
        nodeJs 'my-nodejs' // name that i gave the instalation in Jenkins UI
    }

    // All stages can run in parallel or sequentially
    // By default, stages run sequentially
    stages {
        // importing a groovy file
        stage("init") {
            steps {
                script{
                    gv = load "myscript.groovy"
                }
            }
        }
        // sequencial stage (azure-Job)
        stage("build") {
            // this stage will run only if the Conditional expression is true
            // in this case it will run oinly if or beanch name i dev and there are changes to it 
            // you have acces to certain environment variables in jenkins such as (BRANCH_NAME, CODE_CHANGES etc ..)
            // google jenkins environment variable for more
            when {
                expression {
                    BRANCH_NAME == "dev" && CODE_CHANGES == true && params.executeTest == true
                }
            }

            // the script block let you wirte groovy scipt directly 
            // execute the script of the function in myscript.groovy
            script {
                    gv.buildApp()
                }

            steps {
                echo 'Building the application...'
                echo "Building application ${NEW_VERSION}"
            }
        }
        
        // Parallel stage (azure-Job)
        // The "test" stage contains two parallel stages: "unit_tests" and "integration_tests".
        // These two sub-stages will run concurrently (in parallel).
        stage("test") {
            // execute the script of the function in myscript.groovy
            script {
                    gv.testApp()
                }
            // stage execute only if the branch name is "dev" or "main"

            when {
                expression {
                    BRANCH_NAME == "dev" || BRANCH_NAME == "main"
                }
            }
            parallel {
                stage("unit_tests") {
                    steps {
                        echo 'Running unit tests...'
                    }
                }
                stage("integration_tests") {
                    steps {
                        echo 'Running integration tests...'
                    }
                }
            }
        }
        // The "deploy" stage will only start after The "test" stage is done, which means
        // "unit_tests" and "integration_tests" are completed.
        stage("deploy") {
            // allow user to provide input 
            input {
                message "Select the environment to deploy to"
                ok "Done"
                parameters {
                    choice (name: 'ONE', choices: ['dev', 'staging', 'prod'], description: "versions to chose, to deploy on prod")
                    choice (name: 'TWO', choices: ['dev', 'staging', 'prod'], description: "versions to chose, to deploy on prod")                }
            }

            steps {
                // execute the script of the function in myscript.groovy
                script {
                    // define input inside  script
                    env.ENV = input message: "Select the environment to deploy to", ok: "Done", parameters: [choice(name: 'ONE', choices: ['dev', 'staging', 'prod'], description: "")]
                    gv.deployApp()
                    echo "Deploying to ${ONE}..."
                    echo "Deploying to ${TWO}..."
                }
                echo 'Deploying the application...'
                
                // using parameter
                echo "Deploying the application version ${params.VERSION}..."
                
                // use server credential
                echo "Deploying the application ${SERVER_CREDENTIALS}"
                sh "${SERVER_CREDENTIALS}"

                // or if you need it on one stage
                withCredentials([
                    usernamePassword(credentials: 'server-credentials', usernameVariable: USER, passwordVariable: PWD )
                ]) {
                    sh "some shel command ${USER} ${PWD}"
                }

            }
        }
    }

    // this ia a post block, in it you define expression of 
    // either Build status or Build status changes (when a build failed before but now it succeded an vice versa)
    // the post block will execute after all stages are executed. Google "post-build actions" for more
    post {
        // alway condition
        always{
            // always run after the stages executer. A real life use case 
            // senario could be sending an email to the team to inform 
            // them about the result of a stage or the pipeline in general
            script {
                if (env.CHANGE_HAPPENED) {
                    echo "Changes detected in the following files:"
                    echo env.CHANGE_HAPPENED
                } else {
                    echo "No changes detected."
                }
            }
        }

        // the script withing "sucess" will only run when the build succeded
        sucess {
            
        }

        // the script withing "sucess" will only run when the build failed
        failure {
            
        }
    }
}