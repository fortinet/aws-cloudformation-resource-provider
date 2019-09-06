# Looks at the swagger schema and dumps the JSON output of the specified parameter
import json
import os
import sys
def main():
    name = input("Name")
    configRoot = input("configRoot")
    method = "put"
    print("Name:" , name)
    print(configRoot)

    fileName = "/home/jcripps/Documents/git/aws_uluru/api_master_folder/swagger_api_v2_cmdb_system.json"
    print (fileName)

    with open(fileName, 'r') as f:
        datastore = json.load(f)
        #return json.dumps(thedata)
    toString = str(json.dumps(datastore["paths"]["/system/interface"][method]["parameters"][0]["schema"]["properties"]))[1:-1]
    print (toString)

main()
