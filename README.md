# Time Tracker

Very simple script to log time spent on tasks

### Usage

Start script, at which point the timer will start

```
$ ./timetracker studying
> studying started on 2018-03-09 at 18:00:09.957 local time
```

Stop script, which will stop the timer (the value added will the delta between the time the script was started and stopped):
```
$ ^C    # press ctrl-c to send sigint to stop script
```

Output:
```
> Spent 3756 ms on current studying

> Spent 130872 ms today on studying
> Spent 131872 ms this month on studying
> Spent 132872 ms this year on studying
```

The times will be written to and read from time_tracker_log.txt

### Environment

No external dependencies. Built using Mac OS 10.12 with Scala 2.12.1 with Java 1.8.0_121
