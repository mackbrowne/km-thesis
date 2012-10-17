import json
import psycopg2
import psycopg2.extras
import sys
import pprint
from flask import Flask, request, session, g, redirect, url_for, \
	abort, render_template, flash

# Database configuration parameters
DATABASE = 'traffic_images'
SECRET_KEY = 'password'
USERNAME = 'postgres'
PASSWORD = 'password'

app = Flask(__name__)
app.config.from_object(__name__)

@app.route('/')
def hello_world():
    return 'Hello World!'

# Gets the list of locations and displays them in raw JSON
@app.route('/q/locations')
def get_locs():
	return "Locations"

# Returns an image and displays it given groupname,locid,timestamp
@app.route('/q/image')
def get_img():	
	groupname = request.args.get("group")
	locid = request.args.get("locid")
	timestamp = request.args.get("time")
	conn_string = "host='localhost' dbname='"+DATABASE+"' user='"+USERNAME+"' password='"+PASSWORD+"'"
	try:
		conn = psycopg2.connect(conn_string)
		cursor = conn.cursor('imageget', cursor_factory=psycopg2.extras.DictCursor)
		cursor.execute('SELECT * FROM FRAMES LIMIT 10')
		record = cursor.fetchone()
		conn.close();
		return record[0]+"  :  " + record[1] + "  :  " + record[2]
	except:
		# Get the most recent exception
		exceptionType, exceptionValue, exceptionTraceback = sys.exc_info()
		# Exit the script and print an error telling what happened.
		sys.exit("Database connection failed!\n ->%s" % (exceptionValue))
	
	return "image"
		
if __name__ == '__main__':
    app.run()