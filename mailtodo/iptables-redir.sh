export IPADDRESS=`hostname -I`

# redirect FROM TO PROTOCOL
# setup port redirect using iptables
redirect() {
echo "Redirecting port $1 to $2 ($3)"
	iptables -t nat -A PREROUTING -p $3 --dport $1 -j REDIRECT --to-ports $2
	iptables -t nat -A OUTPUT -d localhost -p $3 --dport $1 -j REDIRECT --to-ports $2
	# Add all your local ip adresses here that you need port forwarding for
	for ip in $IPADDRESS
	do
		iptables -t nat -A OUTPUT -d $ip -p $3 --dport $1 -j REDIRECT --to-ports $2
	done
}

block() {
	echo "Blocking port $1"
	iptables -A INPUT -p tcp --dport $1 -s localhost -j ACCEPT
	for ip in $IPADDRESS
	do
		iptables -A INPUT -p tcp --dport $1 -s $ip -j ACCEPT
	done
	iptables -A INPUT -p tcp --dport $1 -j REJECT
}


redirect 2525 25 tcp
