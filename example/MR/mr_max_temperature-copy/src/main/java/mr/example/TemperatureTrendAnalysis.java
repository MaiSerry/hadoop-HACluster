package mr.example;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class TemperatureTrendAnalysis {
    
    
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: TemperatureTrendAnalysis <input path> <output path>");
            System.exit(-1);
        }

        Job job = Job.getInstance();
        job.setJarByClass(TemperatureTrendAnalysis.class);
        job.setJobName("Temperature Trend Analysis");

        job.setMapperClass(TrendMapper.class);
        job.setReducerClass(TrendReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
    /**
     * Mapper:
     * Input: "1901-01 25.0"
     * Output: Key="01", Value="1901-01|25.0"
     */
    public static class TrendMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString().trim();
            if (line.isEmpty()) return;

            // Split by whitespace (tab or space)
            String[] parts = line.split("\\s+");
            if (parts.length < 2) return;

            String yearMonth = parts[0]; // e.g., 1901-01
            String temperature = parts[1]; // e.g., 25.0
            
            // Extract the month part (MM) from YYYY-MM
            if (yearMonth.length() >= 7) {
                String month = yearMonth.substring(5, 7);
                context.write(new Text(month), new Text(yearMonth + "|" + temperature));
            }
        }
    }
    
    public static class TrendReducer extends Reducer<Text, Text, Text, Text> {

        private static class Entry {
            String yearMonth;
            double temp;

            Entry(String ym, double t) {
                this.yearMonth = ym;
                this.temp = t;
            }
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            List<Entry> history = new ArrayList<>();

            for (Text val : values) {
                String[] parts = val.toString().split("\\|");
                history.add(new Entry(parts[0], Double.parseDouble(parts[1])));
            }

            // Sort chronologically by YYYY-MM
            Collections.sort(history, Comparator.comparing(o -> o.yearMonth));

            int totalScore = 0;
            // Compare each year with the previous year
            for (int i = 1; i < history.size(); i++) {
                double prevTemp = history.get(i - 1).temp;
                double currTemp = history.get(i).temp;

                if (currTemp > prevTemp) {
                    totalScore += 1;
                } else if (currTemp < prevTemp) {
                    totalScore -= 1;
                }
                // If equal, score change is 0
            }

            // Determine final status
            String status;
            if (totalScore > 0) {
                status = "Warming";
            } else if (totalScore < 0) {
                status = "Cooling";
            } else {
                status = "Not changing";
            }

            // Output: Month  Status
            context.write(key, new Text(status));
            
            // Optional: Include the score for debugging
            // context.write(key, new Text(status + " (Score: " + totalScore + ")"));
        }
    }



}
