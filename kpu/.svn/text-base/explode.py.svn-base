from sqlite3 import connect
import sys, os, re
from time import time

dbfile = sys.argv[1]
db = connect(dbfile)
root = os.path.join(".", "401_Cameras")
s = time()

c = db.cursor()
c.execute("""
  select distinct locname from locations
  where locname like '401%'
  """)
for loc in [x[0] for x in c.fetchall()]:
  dir = os.path.join(root, re.sub(r'\W', '_', loc).strip("_"))
  print dir
  try:
    os.makedirs(dir)
  except:
    pass
  c.execute("""
    select strftime('%Y-%m-%d_%H-%M-%S', 
                     timestamp/1000, 'unixepoch', 'localtime'), jpg
    from frames join locations using (groupname, locid)
    where locname = ?
  """, (loc,))
  for row in c.fetchall():
    with open(os.path.join(dir, "%s.jpg" % row[0]), "w") as f:
      f.write(row[1])

print "All done in ", (time() - s)
