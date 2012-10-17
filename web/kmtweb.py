from flask import Flask, request, render_template, make_response, g, url_for, redirect
from sqlite3 import connect
import json
from datetime import datetime
from collections import defaultdict

DB_FILE = "../../traffic-images-with-loc.sqlite"

def format_timestamp(t, format="%Y/%m/%d %H:%M:%S"):
  d = datetime.fromtimestamp(t/1000)
  return d.strftime(format)

app = Flask(__name__)

@app.before_request
def before_request():
  g.db = connect(DB_FILE)

@app.teardown_request
def teardown_request(exception):
  g.db.close()


def index():
  c = g.db.cursor()
  c.execute("""
    select groupname, locname, locid
    from locations
    order by groupname, locname
  """)
  locs = defaultdict(list)
  for (group, name, id) in c.fetchall():
    locs[group].append(dict(name=name, id=id))
  return render_template("index.html", locs=locs)

@app.route("/image")
def get_image():
  group = request.args.get("group")
  locid = request.args.get("locid")
  timestamp = int(request.args.get("timestamp"))

  cursor = g.db.cursor()
  cursor.execute("""
    select jpg 
    from frames 
    where groupname=? and locid=? and timestamp>=?
    limit 1
  """, (group, locid, timestamp))
  results = cursor.fetchall()
  if not results:
    return "Not found"
  else:
    jpg = results[0][0]
    response = app.response_class(jpg)
    response.headers['Content-Type'] = 'image/jpeg'
    return response

@app.route("/next")
def get_nexttimestamp():
  group = request.args.get("group")
  locid = request.args.get("locid")
  timestamp = request.args.get("timestamp")
  c = g.db.cursor()
  c.execute("""
    select timestamp
    from frames
    where groupname=? and locid=? and timestamp>? limit 1
  """, (group, locid, timestamp))
  try:
    ntimestamp = c.fetchall()[0][0]
  except:
    ntimestamp = None

  return json.dumps(ntimestamp)

@app.route("/movie/<group>/<path:locid>")
def movie(group, locid):
  c = g.db.cursor()
  c.execute("""
    select locname from locations
    where groupname=? and locid=?
    """, (group, locid))
  try:
    locname = c.fetchone()[0]
  except:
    locname = "Unknown location"

  return render_template("movie.html", group=group, locid=locid, locname=locname)

@app.route("/map")
def map():
  c = g.db.cursor()
  c.execute("select * from locations")
  urls = ""
  
  try:
    locs = c.fetchall()
  except:
    locs = "nodata"
    
  for locnum in range(len(locs)):
    urls = urls + url_for('movie', group=locs[locnum][0], locid=locs[locnum][1]) + ","
    
  return render_template("map.html", locations=json.dumps(locs), urls=urls)

@app.route("/maplayout")
def maplayout():
  c = g.db.cursor()
  c.execute("select * from locations")
  urls = ""
  
  try:
    locs = c.fetchall()
  except:
    locs = "nodata"
    
  for locnum in range(len(locs)):
    urls = urls + url_for('movielayout', group=locs[locnum][0], locid=locs[locnum][1]) + ","
    
  return render_template("mapbody.html", locations=json.dumps(locs), urls=urls)

@app.route("/listlayout")
def listlayout():
  c = g.db.cursor()
  c.execute("""
    select groupname, locname, locid
    from locations
    order by groupname, locname
  """)
  locs = defaultdict(list)
  for (group, name, id) in c.fetchall():
    locs[group].append(dict(name=name, id=id))
  return render_template("listbody.html", locs=locs)
  
@app.route("/movielayout/<group>/<path:locid>")
def movielayout(group, locid):
  c = g.db.cursor()
  c.execute("""
    select locname from locations
    where groupname=? and locid=?
    """, (group, locid))
  try:
    locname = c.fetchone()[0]
  except:
    locname = "Unknown location"

  return render_template("moviebody.html", group=group, locid=locid, locname=locname)

@app.route("/")
def homepagelayout():
    return render_template("homepagebody.html")

if __name__ == '__main__':
  app.run(host="0.0.0.0", port=9100, debug=True)
