# Bash Script to automate the steps of initializing and installing required packages.
# Call with printf <api name> | ./install.sh (example: printf dns | ./install.sh)
#Next steps:
# JSON TEMPLATE UPDATE URL
# Add dynamic file paths that don't include  my name
# Add in templat
# Add MVN install and cfn-cliu generate
echo "API Name:"
read Name
echo ${Name}
mkdir ${Name}
configRoot='system'

cp -avp /home/jcripps/Documents/git/aws_uluru/aws-cloudformation-rpdk-nightly.zip ./${Name}
unzip ./${Name}/aws-cloudformation-rpdk-nightly.zip -d ./${Name}/
#The AWS install script requires we be in the working directory:
cd ./${Name}
./install.sh
#activate ENV - This allows mvn calls to work.
source /home/jcripps/Documents/git/aws_uluru_master/./${Name}/env/bin/activate
#Pipe the package and resoruce name to the python install script
printf 'Fortinet::FortiGate::System'${Name}'\n\n' | cfn-cli init


cp -avp  /home/jcripps/Documents/git/aws_uluru/api_master_folder/fortinet-fortigate-system.json ./
mv ./fortinet-fortigate-system.json ./fortigate-${configRoot}-${Name}.json

tmp=$(printf ${Name}'\n'${configRoot}'\n' | python /home/jcripps/Documents/git/aws_uluru/api_master_folder/disectAndAddJSON.py)
#Currently just add parsed api request into line 16 of the template.
awk -i inplace -v x="$tmp" 'NR==16{print x} 1' ./fortigate-${configRoot}-${Name}.json

#The Following works, but is limited since all vars must have a value:

#jq -n --argjson url 0 -f ./fortigate-${configRoot}-${Name}.json
#jq '.sourceUrl = githubcom' + ${configRoot} + ${Name} ./fortigate-${configRoot}-${Name}.json | tee test.json # ./fortigate-${configRoot}-${Name}.json
