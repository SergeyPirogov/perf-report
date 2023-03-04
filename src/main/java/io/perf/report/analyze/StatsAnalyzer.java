package io.perf.report.analyze;

import io.perf.report.context.Simulation;
import io.perf.report.model.SimulationStats;
import io.perf.report.model.RequestStats;
import io.perf.report.model.SimulationRequest;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class StatsAnalyzer {

    public static SimulationStats computeSimulationStats(Simulation simulation) {
        List<SimulationRequest> requests = simulation.getRequests();
        System.out.println();

        SimulationStats analyticsResult = new SimulationStats();

        requests.forEach(req -> {
            int maxUsers = simulation.getMaxUsers(req.getScenario());
            RequestStats requestStatistics = computeRequestStats(req, maxUsers);
            requestStatistics.setRequestName(req.getRequest());
            requestStatistics.setIndice(req.getIndice());
            requestStatistics.setSimulationName(req.getSimulation());
            requestStatistics.setScenario(req.getScenario());
            requestStatistics.setStart(req.getStart());
            requestStatistics.setEnd(req.getEnd());

            analyticsResult.addResult(requestStatistics);
        });

        analyticsResult.getResults().sort(Comparator.comparing(RequestStats::getIndice));
        return analyticsResult;
    }

    private static RequestStats computeRequestStats(SimulationRequest request, int maxUsers) {
        double[] times = request.getDurationsAsArray();
        long min = (long) StatUtils.min(times);
        long max = (long) StatUtils.max(times);

        double sum = 0;
        for (double d : times)
            sum += d;

        double avg = sum / times.length;

        long p50 = (long) StatUtils.percentile(times, 50.0);
        long p90 = (long) StatUtils.percentile(times, 90.0);
        long p95 = (long) StatUtils.percentile(times, 95.0);
        long p99 = (long) StatUtils.percentile(times, 99.0);

        StandardDeviation stdDev = new StandardDeviation();
        long stddev = (long) stdDev.evaluate(times, avg);
        double duration = (request.getEnd() - request.getStart()) / 1000.0;

        String startDate = getDateFromInstant(request.getStart());
        long successCount = request.getCount() - request.getErrorCount();
        double rps = successCount / duration;
        
        RequestStats requestStatistics =  new RequestStats();
        requestStatistics.setMin(min);
        requestStatistics.setMax(max);
        requestStatistics.setAvg(avg);
        requestStatistics.setP50(p50);
        requestStatistics.setP90(p90);
        requestStatistics.setP95(p95);
        requestStatistics.setP99(p99);
        requestStatistics.setStddev(stddev);
        requestStatistics.setDuration(duration);
        requestStatistics.setMaxUsers(maxUsers);
        requestStatistics.setRps(rps);
        requestStatistics.setStartDate(startDate);
        requestStatistics.setSuccessCount(successCount);
        requestStatistics.setCount(request.getCount());
        requestStatistics.setErrorCount(request.getErrorCount());

        return requestStatistics;
    }

    private static String getDateFromInstant(long start) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd " + "HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(start));
    }
}
