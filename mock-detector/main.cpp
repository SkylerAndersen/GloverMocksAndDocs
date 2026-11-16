#include <iostream>
#include <string>

#include <ctime>
#include <cstdlib>

void log_output () {
	const char* possible_words[] = {"Hello","Hi","Goodbye","This","That","NULL"};
	std::srand(static_cast<unsigned int>(std::time(nullptr)));
	int index = std::rand() % 5;
	std::cout << std::to_string(index) << ' ';
	index = std::rand() % 5;
	std::cout << possible_words[index] << std::endl;
}

void log_usage () {
	std::cerr << "usage : chord-detector <--IP|-i> <http://ip:<80|8080>> "
			<< "<--Latest-Index|-l> <0|1|2|3|4>" << std::endl;
}

int main (int argc, char** argv) {
	if (argc != 5) {
		std::cerr << "Arg count is off" << std::endl;
		log_usage();
		return 1;
	}

	// determine if flags are set
	bool hasIpFlag = !std::strcmp(argv[1],"--IP") || !std::strcmp(argv[1],
				"-i");
	bool hasIndexFlag = !std::strcmp(argv[3],"--Latest-Index") || 
				!std::strcmp(argv[3],"-l");

	// if no flags, log usage and error out
	if (!hasIpFlag || !hasIndexFlag) {
		std::cerr << "Missing flags." << std::endl;
		log_usage();
		return 1;
	}

	// check if argv[2] is ip shaped
	bool hasIP = true;
	bool has80 = false;
	bool has8080 = false;
	if (std::strncmp("http://",argv[2],7))
		hasIP = false;
	int len = std::strlen(argv[2]);
	if (argv[2][len-1] != '0' || argv[2][len-2] != '8') // must end in 80
		hasIP = false;

	if (!hasIP) {
		std::cerr << "Missing protocol or doesn't end in 80 family port." << std::endl;
		log_usage();
		return 1;
	}

	// check that the port is 80 or 8080
	if (argv[2][len-3] == ':')
		has80 = true;

	if (argv[2][len-3] == '0' && argv[2][len-4] == '8' &&
		argv[2][len-5] == ':')
		has8080 = true;

	if (!has80 && !has8080) {
		log_usage();
		return 1;
	}

	// check if the last arg is an int 0-4
	bool isOneDigit = argv[4][1] == '\0';
	if (!isOneDigit) {
		std::cerr << "doesn't end in :80 or :8080" << std::endl;
		log_usage();
		return 1;
	}

	int digit = argv[4][0] - 48;
	if (digit < 0 || digit > 4) {
		std::cerr << "index invalid" << std::endl;
		log_usage();
		return 1;
	}
	
	// if all are right, run the log output method
	log_output();
	
	return 0;
}
