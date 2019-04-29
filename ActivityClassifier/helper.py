import re
with open("/home/oemer/Documents/data.txt","r+")as fd,open("/home/oemer/Documents/data_v2.txt","w+")  as out:
    text = fd.readlines()
    for x in range(0,len(text)):
        line = re.split(",| ",text[x])
        line = list(filter(None,line))
        out.write(",".join(line))