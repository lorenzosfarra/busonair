package boa.server.config;

public interface BoaConfig {
	int
	// Supported days
			DAYS = 7,
			DAYS_IN_MINUTES = 10080,
			// Limit of supported days when performing a research for trips, in
			// minutes
			SEARCH_DAYS_LIMIT = 2,
			SEARCH_DAYS_LIMIT_IN_MINUTES = 2880,
			MAX_WALK_DISTANCE = 1000, TRANSACTIONS_LIMIT = 1000;
}
