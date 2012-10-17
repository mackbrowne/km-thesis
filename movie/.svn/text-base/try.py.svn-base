import sqlite3
import sys, os
from PIL import Image
from StringIO import StringIO

N = 100
DB = sys.argv[1]
if os.path.exists(DB):
  db = sqlite3.connect(DB)
else:
  print "%s does not exist" % DB
  sys.exit(0)


c = db.cursor()
c.execute("select locid from locations limit 9")
locids = [x[0] for x in c.fetchall()]

print "Compiling movies for %s" % locids

jpgs = dict()
for locid in locids:
  print "Loading %d frames from location %s" % (N, locid)
  c.execute("""
    select jpg 
    from frames 
    where locid = ? 
    order by timestamp limit %d""" % N, (locid,))
  jpgs[locid] = [x[0] for x in c.fetchall()]

for i in range(N):
  images = []
  for (n, locid) in enumerate(locids):
    im = Image.open(StringIO(jpgs[locid][i]))
    im = im.crop((0,50,320,180))
    images.append(im)
  w, h = images[0].size
  frame_im = Image.new('RGBA', (w*3, h*3))
  for (n, im) in enumerate(images):
    row, col = n / 3, n % 3
    x, y = w * row, h * col
    alpha = n+100
    frame_im.paste(im, (x, y))
  frame_im.save("%.3d.jpg" % i)
  print "Done with frame %d" % i

# ffmpeg -r 20 -i %03d.jpg out.mp4
