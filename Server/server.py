#!/usr/bin/env python
# coding: utf8
#Server for app  Speech-to-Text Messenger. By Pristavka Egor
from BaseHTTPServer import HTTPServer
from BaseHTTPServer import BaseHTTPRequestHandler
import json
import random
import speech_recognition as sr
import base64
usrs = []
nicks = {}
tokns = {}
messgs ={}
friends = {}
nickes = []
lastmess = {}
avatars = {}


HOST = "194.176.114.21"
PORT = 8010
FILE_PREFIX = "."


class JSONRequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):

        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.wfile.write("\r\n")

        try:
            output = open(FILE_PREFIX + "/" + self.path[1:] + ".json", 'r').read()
        except Exception:
            output = "{'error': 'Could not find file " + self.path[1:] + ".json'" + "}"
        self.wfile.write(output)

    def do_POST(self):
        global allcards, cards_left
        self.send_response(200)
        self.wfile.write('Content-Type: application/json\r\n')
        self.wfile.write('Client: %s\r\n' % str(self.client_address))
        self.wfile.write('User-agent: %s\r\n' % str(self.headers['user-agent']))
        self.wfile.write('Path: %s\r\n' % self.path)
        self.data_string = self.rfile.read(int(self.headers['Content-Length']))
        self.end_headers()
        data = json.loads(self.data_string)
        if 'action' in data:
            print "action = " + data['action']
            if data['action'] == 'register':
                # register player
                 if data['nickname'] in nicks.values():
                        print 'Nickname ' + data['nickname'] + ' is already registered'
                        self.wfile.write(json.dumps(
                            {"status": "error", "token" : -1}
                        ))

                 else:
                    token = random.randint(1000000, 9000000)
                    usr={"nick":data['nickname'],"pass":data['password'],"pin":data['pin']}
                    print usr;
                    nicks[token] = data['nickname']
                    tokns[data['nickname']] = token
                    usrs.append(usr)
                    messgs[data['nickname']] = {}
                    friends[data['nickname']] = []
                    nickes.append(data['nickname'])


                    print 'Nickname ' + data['nickname'] + ' is registered with token ' + str(token) + '\n'
                       
                    self.wfile.write(json.dumps(
                   { "status": "ok", "token": token}
         
                   ))
            if data['action'] == 'login':
                usver = {"nick":data['nickname'],"pass":data['password'],"pin":data['pin']}
                
                if usver in usrs:
                   token = tokns[data['nickname']]

                   self.wfile.write(json.dumps(
                   {"status":"loggined","token":token}
                   ))
                else:
                    self.wfile.write(json.dumps(
                    {"status": "error", "token" : -1}
                    ))

            if data['action']=='send_mess':
               rec_m='none'
               if data['voice']!='nil':
                 image_64_decode = base64.decodestring(data['voice'])
                 image_result = open('audio.wav', 'wb') 
                 image_result.write(image_64_decode)
                 r=sr.Recognizer()
                 PATH='audio.wav'
                 with sr.AudioFile(PATH) as source:
                   audio=r.record(source)
                   rec_m=r.recognize_google(audio,language = 'ru')
		               #print rec_m
               if data['sender'] not in messgs[data['reciver']]:
                 message = {"text":data['text'],"picture":data['picture'],"voice":data['voice'],   "voice_text":rec_m,"file":data['file'],"filereso":data['filereso'],"pos":1}
                 messgs[data['reciver']][data['sender']]=[]
                 messgs[data['reciver']][data['sender']].append(message)
                 self.wfile.write(json.dumps(
                 {"status":"sended"}
                 ))
               else:
		 pos=len(messgs[data['sender']][data['reciver']])+len(messgs[data['reciver']][data['sender']])+1 
                   #pos=len(messgs[data['reciver']][data['sender']])+1 
                 message = {"text":data['text'],"picture":data['picture'],"voice":data['voice'],   "voice_text":rec_m,"file":data['file'],"filereso":data['filereso'],"pos":pos}   
                 messgs[data['reciver']][data['sender']].append(message)
                 self.wfile.write(json.dumps(
                 {"status":"sended"}
                 ))
                

            if data['action']=='get_mess':
             if data['sobesednik'] not in messgs[data['nickname']]: 
                messgs[data['sobesednik']][data['nickname']]=[]
             if data['sobesednik'] not in messgs[data['nickname']]: 
                messgs[data['nickname']][data['sobesednik']]=[]  
             self.wfile.write(json.dumps(
              {"status":"SUC","Your":messgs[data['sobesednik']][data['nickname']],"its":messgs[data['nickname']][data['sobesednik']]}
              ))
            if data['action'] == 'find_men':
              humans_names=[]
	      humans_avatars=[]	
              for name in nickes:
                 if len(data['person'])<=len(name):
                   if data['person'] in name:
		     humans_names.append(name)	
	             humans_avatars.append(avatars[name])
              self.wfile.write(json.dumps(
              {"names":humans_names,"avatars":humans_avatars}
              ))
              

            if data['action'] == 'add_friend':
              frient={"nickname":data['person'],"avatar":avatars[data['person']]} 
              frient1={"nickname":data['nickname'],"avatar":avatars[data['nickname']]} 
              if frient in friends[data['nickname']]:
                self.wfile.write(json.dumps(
               {"status":"error"}
               ))
              else:
               friends[data['nickname']].append(frient)
               friends[data['person']].append(frient1) 
               self.wfile.write(json.dumps(
               {"status":"ADDED!"}
               ))
            if data['action'] == 'get_friendlist':

             self.wfile.write(json.dumps(
             {"friends":friends[data['nickname']]}
             ))

	    if data['action']=='delete_friend':
             frient={"nickname":data['person'],"avatar":avatars[data['person']]}
	     frient1={"nickname":data['nickname'],"avatar":avatars[data['nickname']]}
             friends[data['nickname']].remove(frient)
             friends[data['person']].remove(frient1)
	     self.wfile.write(json.dumps({"Status":"DONE!"}))		
             
          
            if data['action'] == 'avatar':
              avatars.update({data['nickname']:data['image']})
	      print data['image']
              self.wfile.write(json.dumps({"Status":"OK","IMAGE":avatars[data['nickname']]}))


            
server = HTTPServer((HOST, PORT), JSONRequestHandler)
server.serve_forever()
