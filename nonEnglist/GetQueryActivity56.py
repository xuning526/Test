#!/usr/bin/env python
#-*- coding:utf-8 -*-

import urllib2,cookielib,urllib,time, os.path, logging, datetime


HOST = "192.168.20.113"
PORT = "8080"
ADMIN = "admin"
PASSWORD = "123456"
SAVE_TO = "e:\\user\\"
LIFE_OF_LOG = 30    # defualt is 30 days

HUB_LOGIN_URL="http://%(HOST)s:%(PORT)s/security_check" % locals()
GENERATE_REPORT_URL = "http://%(HOST)s:%(PORT)s/status/reportgeneration.html?status=statistic.running"  % locals()
DOWNLOAD_REPORT_URL = "http://%(HOST)s:%(PORT)s/status/reportlogfiles.html?" % locals()


sh = logging.StreamHandler()
sh.setLevel(logging.DEBUG)
root = logging.getLogger()
root.setLevel(logging.DEBUG)
root.addHandler(sh)
LOG = logging.getLogger(__name__)

class AuthException(BaseException):
    """docstring for AuthException"""
    def __init__(self, arg):
        self.arg = arg

    def __repr__(self):
        return self.arg
        

class Hub(object):

    def __init__(self):
        self.cj = cookielib.CookieJar()
        self.opener = urllib2.build_opener(urllib2.HTTPCookieProcessor(self.cj))

    def save_current_user_activity(self):

        from_date = to_date = time.strftime("%Y-%m-%d")
        cur_date = time.strftime("%Y%m%d")
        filename = "Krugle-UserQueries-%s.csv" % cur_date
        self._login()
        self._generate_activity(from_date, to_date)
        LOG.info("Wait 2 minus for producing the log file ")
        time.sleep(2)
        self._download(filename, cur_date)
        self._delete()


    def _login(self):
        data = urllib.urlencode({"j_username":ADMIN, "j_password":PASSWORD})
        req = self.opener.open(HUB_LOGIN_URL,data = data )
        ret = req.read()
        if ret.find('action="security_check"') != -1: # login failed
            raise AuthException("authentication failed , please check you admin username/password: username:%(ADMIN)s password:%(PASSWORD)s" % globals())
        LOG.info("log in successfully")

    def _generate_activity(self, from_date, to_date):
        generate_data = urllib.urlencode({"type":"query",
            "from":from_date,
            "to":to_date,
            "submit":"Generate"})
        req = self.opener.open(GENERATE_REPORT_URL, data = generate_data)

    def _download(self, filename, cur_date):
        params = urllib.urlencode({"folder":filename})
        url = DOWNLOAD_REPORT_URL + params
        LOG.debug("Request url: %s to download file", url)
        req = self.opener.open(url)
        save_filename = "KrugleAccess%s.csv" % cur_date
        file_path = os.path.join(SAVE_TO, save_filename)
        LOG.debug("Save the file to %s", file_path)
        csv = open(file_path,"w")
        csv.write(req.read())
        csv.close()

    def _delete(self):
        today = datetime.datetime.now()
        from_day = today - datetime.timedelta(LIFE_OF_LOG)
        for f in os.listdir(SAVE_TO):
            file_date = datetime.datetime.strptime(f, "KrugleAccess%Y%m%d.csv")
            if file_date < from_day:
                os.remove(os.path.join(SAVE_TO, f))


def main():
    LOG.info("start")
    hub = Hub()
    hub.save_current_user_activity()


if __name__ == '__main__':
    main()
