#Dumps formated list for AWS handler and CloudFormations
import json
import os
import sys
def main():

    fileName = "/home/jcripps/Documents/git/aws_uluru/api_master_folder/names.json"
    print (fileName)

    with open(fileName, 'r') as f:
        datastore = json.load(f)

    for (i,j) in datastore["properties"].items():
         print (r'"\"'+ str.lower(i) + r'\":" +'+ r' "\""' + " + model.get" + i + "() + " + r'"\"" + ' + r' ","' + " +")

             #Cloud Formations dump
    for (i,j) in datastore["properties"].items():
        print ("\"" + i + "\"" + ":{\"Ref\":" + "\"" + i + "\"}," )
    print ("\n")
    for (i,j) in datastore["properties"].items():
        if j == "description":
            print (str.capitalize(j))


main()
