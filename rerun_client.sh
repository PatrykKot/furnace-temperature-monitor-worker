ps -ef | grep 'furnace-temperature-monitor-client' | grep -v grep | awk '{print $2}' | xargs -r kill -9 &&
git pull &&
(cd client && ./gradlew build) &&
(cd client/build/libs &&  nohup java -jar Furnace-temperature-monitor-client-1.0-SNAPSHOT.jar --host localhost --port 8081 --mocked --mockedSensors 5 >& /dev/null &) &&
ps -ef | grep 'gradle' | grep -v grep | awk '{print $2}' | xargs -r kill -9
