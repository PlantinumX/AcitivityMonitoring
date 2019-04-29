import re
with open("actitracker_raw.txt","r+")as fd,open("/home/oemer/Documents/data_v2.txt","w+")  as out:
    text = fd.readlines()
    for x in range(0,len(text)):
        line = re.split(",| ",text[x])
        for i in range(len(line)):
            if line[i] == "Downstairs" or line[i] == "Upstairs" or line[i] == "Jogging":
                line[i] = "Walking"
        line = list(filter(None,line))
        out.write(",".join(line))