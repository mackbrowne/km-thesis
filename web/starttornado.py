from kmtweb import app
import kmtweb
import sys, os

def die():
  print >>sys.stderr, "Databasefile does not exist."
  sys.exit(0)
try:
  dbfile = sys.argv[1]
  if not os.path.exists(dbfile): die()
  else:
    kmtweb.DB_FILE = dbfile
except:
  die()

from tornado.wsgi import WSGIContainer
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop

http_server = HTTPServer(WSGIContainer(app))
http_server.listen(9100)
IOLoop.instance().start()

