def buildJar() {
    echo "Building the applicattion..."
    sh "mvn package"
}

def buildImage() {
    echo "Building the docker image..."
    // accessing our docker hub credention from jenkins
    withCredentials([usernamePassword(credentialsId: "docker-hub-repo", passwordVariable: "PASS", usernameVariable: "USER")]){
        // create docker image, sigin to docker hub and push or image to the demo-app repo
        sh "docker build -t sergevismok/demo-app:jma-2.0 ."
        sh "echo $PASS | docker login -u $USER --password-stdin"
        sh "docker push sergevismok/demo-app:jma-2.0"
    }
}

def deployApp() {
    echo "Deploying the application..."
}

return this