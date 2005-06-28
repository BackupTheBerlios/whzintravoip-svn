package de.fh_zwickau.pti.whzintravoip.thin_client.rtp_comm;

import java.util.*;
import javax.media.*;
import javax.media.control.*;
import javax.media.format.*;
import javax.media.protocol.*;

/**
 * <p>Title: WHZintraVoIP</p>
 *
 * <p>Description: Voice communication over RTP</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author H. Seidel (hs@fh-zwickau.de)
 * @version 1.0
 */
public class VoIP_Process implements ControllerListener {

    private VoIP_Status m_Status = new VoIP_Status();

    // global variable for selection states
    public static int m_iCapture = 0;
    public static int m_iReceive = 1;

    // intern used
    private Processor m_ProcCap = null, m_ProcRec = null;
    private Vector m_DeviceList = null;
    private CaptureDeviceInfo m_DeviceInfo = null;
    private DataSource m_CaptureRtpEncoded = null;
    private DataSource m_RtpSource = null;
    private DataSource m_RtpDecoded = null;
    private static int m_iTimeout = 5000; // 5 seconds till timeout

    // Processor status variables
    private boolean m_bConfigured = false;
    private boolean m_bRealized = false;
    private boolean m_bPrefetched = false;
    private boolean m_bFailed = false;
    private boolean m_bClosed = false;


    /**
     * A new instance.
     */
    public VoIP_Process() {
    }

    /**
     * Set the timeout for processors.
     *
     * @param timeout int timeout in millis
     */
    public void setTimeout(int timeout) {
        this.m_iTimeout = timeout;
    }

    /**
     * Initialize a processor.
     *
     * @param what int The type of processor (global defined)
     * @param source DataSource The Source of Processor to process
     * @return DataSource The Processor output
     * @throws Exception any to parent
     */
    public DataSource initProcessing(int what, DataSource source) throws
            Exception {
        switch (what) {
        case 0:
            initProcCapture();
            m_CaptureRtpEncoded = m_ProcCap.getDataOutput();
            m_Status.infoMessage("Capture processor init!");
            return m_CaptureRtpEncoded;

        case 1:
            initProcRtpDecode(source);
            m_RtpDecoded = m_ProcRec.getDataOutput();
            m_Status.infoMessage("Receive processor init!");
            return m_RtpDecoded;
        default:
            m_Status.errMessage("Wrong integer set !!! no action here ...");
            return null;
        }
    }

    /**
     * Start a Processor. Porcessor must be initialized first.
     *
     * @param what int The typ of Processor
     * @throws Exception any to parent
     */
    public void startProcessing(int what) {
        switch (what) {
        case 0:
            if (m_ProcCap != null) {
                m_ProcCap.start();
                m_Status.infoMessage("Processor started for capture!");
                break;
            } else {
                m_Status.errMessage("Capture processor not initialized yet!");
                break;
            }
        case 1:
            if (m_ProcRec != null) {
                m_ProcRec.start();
                m_Status.infoMessage("Processor started for receive stream!");
                break;
            } else {
                m_Status.errMessage("Receive processor not initialized yet!");
                break;
            }
        default:
            m_Status.errMessage("Wrong integer set !!! no action here ...");
        }
    }

    /**
     * Stop Processing. Processor must be initialized first.
     *
     * @param what int Typ of Processor
     * @throws Exception any to parent
     */
    public void stopProcessing(int what) {
        switch (what) {
        case 0:
            if (m_ProcCap != null) {
                m_ProcCap.stop();
                m_Status.infoMessage("Processor stopt for capture!");
                break;
            } else {
                m_Status.errMessage("Capture processor not initialized yet!");
                break;
            }
        case 1:
            if (m_ProcRec != null) {
                m_ProcRec.stop();
                m_Status.infoMessage("Processor stopt for receive stream!");
                break;
            } else {
                m_Status.errMessage("Receive processor not initialized yet!");
                break;
            }
        default:
            m_Status.errMessage("Wrong integer set !!! no action here ...");
        }
    }

    /**
     * Close Processor. Processor must be initialized first.
     *
     * @param what int Typ of Processor
     * @throws Exception any to parent
     */
    public void closeProcessor(int what) {
        switch (what) {
        case 0:
            if (m_ProcCap != null) {
                synchronized(this){
                    long time = System.currentTimeMillis();
                    try {
                        m_ProcCap.deallocate();
                        m_ProcCap.close();
                        while (!m_bClosed && !m_bFailed) {
                            Thread.sleep(10);
                            if ((System.currentTimeMillis() - time) >
                                m_iTimeout) {
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        m_Status.errMessage("Close Processor: " + ex.toString());
                    }
                }
                m_ProcCap.removeControllerListener(this);
                m_Status.infoMessage("Capture processor closed!");
                break;
            } else {
                m_Status.errMessage("Capture processor not initialized yet!");
                break;
            }
        case 1:
            if (m_ProcRec != null) {
                synchronized(this){
                    long time = System.currentTimeMillis();
                    try {
                        m_ProcRec.deallocate();
                        m_ProcRec.close();
                        while (!m_bClosed && !m_bFailed) {
                            Thread.sleep(10);
                            if ((System.currentTimeMillis() - time) >
                                m_iTimeout) {
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        m_Status.errMessage("Close Processor: " + ex.toString());
                    }
                }
                m_ProcRec.removeControllerListener(this);
                m_Status.infoMessage("Receive processor closed!");
                break;
            } else {
                m_Status.errMessage("Receive processor not initialized yet!");
                break;
            }
        default:
            m_Status.errMessage("Wrong integer set !!! no action here ...");
        }
    }

    /**
     * Initialize an RTP decoding Processor.
     *
     * @param source DataSource the RTP stream
     * @throws Exception any to parent
     */
    private void initProcRtpDecode(DataSource source) throws Exception {
        this.m_RtpSource = source;
        m_Status.infoMessage(
                "Trying to create a processor for RTP input Stream ...");
        m_ProcRec = Manager.createProcessor(m_RtpSource);
        m_bConfigured = false;
        m_bRealized = false;
        m_bPrefetched = false;
        m_bFailed = false;
        m_bClosed = false;
        configureProcessor(m_ProcRec);
        setRtpDecodingCodec();
        realizeProcessor(m_ProcRec);
        prefetchProcessor(m_ProcRec);
    }

    /**
     * Initialize an Capture Processor (mikrophone capture).
     *
     * @throws Exception any to parent
     */
    private void initProcCapture() throws Exception {
        m_Status.infoMessage(
                "Trying to create a processor for microphone capture ...");
        // Get the CaptureDeviceInfo for the live audio capture device
        m_DeviceList = CaptureDeviceManager.getDeviceList(new
                AudioFormat(AudioFormat.LINEAR, 8000, 8, 1));
        if (m_DeviceList.size() > 0) {
            m_DeviceInfo = (CaptureDeviceInfo) m_DeviceList.firstElement();
            // Give the Name of found device to Status
            m_Status.infoMessage("Capture device: " +
                                 m_DeviceInfo.getName().toString());
        } else {
            // Exit if we can't find a device
            m_Status.errMessage("Failed to find a device for sound capture!");
            System.exit( -1);
        }

        // Create a Processor for the capture device:
        m_ProcCap = Manager.createProcessor(m_DeviceInfo.getLocator());
        m_bConfigured = false;
        m_bRealized = false;
        m_bPrefetched = false;
        m_bFailed = false;
        m_bClosed = false;
        configureProcessor(m_ProcCap);
        setCaptureEncodingCodec();
        realizeProcessor(m_ProcCap);
        prefetchProcessor(m_ProcCap);
    }

    /**
     * Processor to configured state.
     *
     * @param proc Processor the Processor
     * @throws Exception any to parent
     */
    private void configureProcessor(Processor proc) {
        proc.addControllerListener(this);
        synchronized(this){
            try {
                long time = System.currentTimeMillis();
                proc.configure();
                while (!m_bConfigured && !m_bFailed) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - time) > m_iTimeout) {
                        break;
                    }
                }
            } catch (Exception ex) {
                m_Status.errMessage("Configure Processor: " + ex.toString());
            }
        }
    }

    /**
     * Processor to realized state.
     *
     * @param proc Processor the Processor
     * @throws Exception any to parent
     */
    private void realizeProcessor(Processor proc) {
        synchronized(this){
            try {
                long time = System.currentTimeMillis();
                proc.realize();
                while (!m_bRealized && !m_bFailed) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - time) > m_iTimeout) {
                        break;
                    }
                }
            } catch (Exception ex) {
                m_Status.errMessage("Realize Processor: " + ex.toString());
            }
        }
    }

    /**
     * Processor to prefetched state.
     *
     * @param proc Processor the Processor
     * @throws Exception any to parent
     */
    private void prefetchProcessor(Processor proc) {
        synchronized(this){
            try {
                long time = System.currentTimeMillis();
                proc.prefetch();
                while (!m_bPrefetched && !m_bFailed) {
                    Thread.sleep(10);
                    if ((System.currentTimeMillis() - time) > m_iTimeout) {
                        break;
                    }
                }

            } catch (Exception ex) {
                m_Status.errMessage("Prefetch Processor: " + ex.toString());
            }
        }
    }

    /**
     * Set the Codec for decoding rtp input stream.
     *
     * @throws Exception any to parent
     */
    private void setRtpDecodingCodec() throws Exception {
        m_ProcRec.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW));

        TrackControl[] tc = m_ProcRec.getTrackControls();
        boolean encodingOk = false;

        for (int i = 0; i < tc.length; i++) {
            if (!encodingOk && tc[i] instanceof FormatControl) {
                if (((FormatControl) tc[i]).
                    setFormat(new AudioFormat(AudioFormat.LINEAR, 8000, 8,
                                              1)) == null) {
                    tc[i].setEnabled(false);
                } else {
                    encodingOk = true;
                }
            } else {
                // we could not set this track to Linear, so disable it
                tc[i].setEnabled(false);
            }
        }
    }

    /**
     * Set the codec for the raw captured stream.
     *
     * @throws Exception any to parent
     */
    private void setCaptureEncodingCodec() throws Exception {
        m_ProcCap.setContentDescriptor(new ContentDescriptor(
                ContentDescriptor.RAW));
        TrackControl[] tc = m_ProcCap.getTrackControls();
        boolean encodingOk = false;
        // Go through the tracks and try to program one of them
        for (int i = 0; i < tc.length; i++) {
            if (!encodingOk && tc[i] instanceof FormatControl) {
                if (((FormatControl) tc[i]).
                    setFormat(new AudioFormat(AudioFormat.G723_RTP, 8000, 8,
                                              1)) == null) {
                    tc[i].setEnabled(false);
                } else {
                    encodingOk = true;
                }
            } else {
                // we could not set this track, so disable it
                tc[i].setEnabled(false);
            }
        }
    }

    public synchronized void controllerUpdate(ControllerEvent ce) {
        if (ce instanceof RealizeCompleteEvent) {
            m_bRealized = true;
        } else if (ce instanceof ConfigureCompleteEvent) {
            m_bConfigured = true;
        } else if (ce instanceof PrefetchCompleteEvent) {
            m_bPrefetched = true;
        } else if (ce instanceof ControllerErrorEvent) {
            m_bFailed = true;
        } else if (ce instanceof ControllerClosedEvent) {
            m_bClosed = true;
        } else {
            return;
        }
        notifyAll();
    }
}
