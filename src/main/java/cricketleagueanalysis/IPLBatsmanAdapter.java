package cricketleagueanalysis;

import com.csvbuilder.CSVBuilderFactory;
import com.csvbuilder.CsvBuilderException;
import com.csvbuilder.ICSVBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

public class IPLBatsmanAdapter extends IPLAdapter {
    Map<String, IPLAnalyserDAO> iplDAOMap = new HashMap<>();
    @Override
    public Map<String, IPLAnalyserDAO> loadIplData( String... filePath) throws IPLException {
        Map<String, IPLAnalyserDAO> iplDAOMap = new HashMap<>();
        iplDAOMap = super.loadIplCSVFileData(IPLBatsmanData.class, filePath[0]);
        if(filePath.length==2){
            this.loadBowlerData(filePath[1]);
        }
        return iplDAOMap;
    }

    private void loadBowlerData(String iplFilePath) throws IPLException
    {
        try (Reader reader = Files.newBufferedReader(Paths.get(iplFilePath)))
        {
            ICSVBuilder csvBuilder = CSVBuilderFactory.createCSVBuilder();
            Iterator<IPLBowlerData> csvFileIterator = csvBuilder.getCSVFileIterator(reader, IPLBowlerData.class);
            Iterable<IPLBowlerData> csvIterable = () -> csvFileIterator;
            StreamSupport.stream
                    (csvIterable.spliterator(), false)
                    .map(IPLBowlerData.class::cast).filter(csvPlayer -> iplDAOMap.get(csvPlayer.playerName) != null)
                    .forEach(bowler -> iplDAOMap.get(bowler.playerName).playerName = bowler.playerName);
        }
        catch (IOException e)
        {
            throw new IPLException(e.getMessage(),
                    IPLException.ExceptionType.CENSUS_FILE_PROBLEM);
        } catch (CsvBuilderException e) {
            throw new IPLException(e.getMessage(),
                    IPLException.ExceptionType.CENSUS_FILE_PROBLEM);
        }
    }
}
