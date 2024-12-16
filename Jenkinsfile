node('unix') {
    stage('Git checkout') {
        checkout scm
    }
    stage('Run tests') {
        withMaven(globalMavenSettingsConfig: '', jdk: '', maven: 'Default', mavenSettingsConfig: '', traceability: true) {
            sh '''
                mvn clean test \
                -Dtype.browser=$browser \
                -Dtype.driver=$driver \
                -Ddb.mode=$dbMode
            '''
        }
    }
    stage('Allure') {
        allure includeProperties: false, jdk: '', results: [[path: 'qa-fruit/target/allure-results']]
    }
}