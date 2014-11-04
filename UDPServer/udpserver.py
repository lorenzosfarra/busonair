import SocketServer
from datetime import datetime
import urllib
import urllib2

class MyUDPHandler(SocketServer.BaseRequestHandler):
    """
    self.request consists of a pair of data and client socket, and since
    there is no connection the client address must be given explicitly
    when sending data back via sendto().
    """

    BOA_SERVER_IP = "http://localhost"
    BOA_SERVER_PORT = 8000

    def handle(self):
        data = self.request[0].strip()
        socket = self.request[1]
        args = self.tracker_data_to_json_format(data)
        run = args.pop('run')
        get_params = urllib.urlencode(args)
        url = "%s:%d/runs/%d/rt/updateposition?%s" %(self.BOA_SERVER_IP, 
                                self.BOA_SERVER_PORT, run, get_params)
        print url
        #print "{} wrote:".format(self.client_address[0]),
        #print data
        socket.sendto(data.upper(), self.client_address)
        req = urllib2.Request(url)
        response = urllib2.urlopen(req)
        response_data = response.read()
        print response_data
        socket.sendto(response_data, self.client_address)
    
    def tracker_data_to_json_format(self, data):
      """
      Transform s, the data sent by the GPS tracker, to a JSON format.
      We only need lat and lon (long in the tracker data).
      """
      args = {}
      b = data.replace(":", "").split(" ")
      i = 1
      while i < len(b):
        current_item = b[(i - 1)]
        if current_item == "lat":
                args['lat'] = b[i]
        elif current_item == "long":
                args['lon'] = b[i]
        elif current_item == "imei":
          args['run'] = self.run_from_imei(current_item)
        i += 1
      # Add time information
      now = datetime.now()
      # TODO add the day value, in minute
      mins = int((now - now.replace(hour=0, minute=0, second=0, microsecond=0)).total_seconds() / 60)
      args['time'] = mins
      return args 
    
    def run_from_imei(self, imei):
      """
      Return the RUN associated to the given IMEI.
      """
      #TODO
      return 5

if __name__ == "__main__":
    HOST, PORT = "localhost", 9999
    #HOST, PORT = "UDPServer_IP_Here", 9999
    server = SocketServer.UDPServer((HOST, PORT), MyUDPHandler)
    server.serve_forever()
