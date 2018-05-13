ps -ef | grep 'furnace-temperature-monitor-server' | grep -v grep | awk '{print $2}' | xargs -r kill -9 &&
git pull &&
(cd server && ./gradlew build) &&
(cd server/build/libs &&  nohup java -jar furnace-temperature-monitor-server-0.1.0.jar >& /dev/null &) &&
ps -ef | grep 'gradle' | grep -v grep | awk '{print $2}' | xargs -r kill -9
