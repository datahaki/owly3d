// code by jph
package ch.ethz.idsc.owly3d.lcm.lidar;

import ch.ethz.idsc.owly3d.demo.LidarPointCloud;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8Decoder;
import ch.ethz.idsc.retina.dev.lidar.mark8.Mark8SpacialProvider;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmClient;

public class Mark8LcmRender implements LcmLidarRender {
  public static final int MAX_COORDINATES = 35000 * 32; // TODO very crude upper bound!
  // ---
  private final LidarPointCloud laserPointCloud;

  public Mark8LcmRender(String lidarId) {
    laserPointCloud = new LidarPointCloud(MAX_COORDINATES);
    laserPointCloud.translate[2] = 1.2;
    Mark8Decoder mark8Decoder = new Mark8Decoder();
    Mark8LcmClient mark8LcmClient = new Mark8LcmClient(mark8Decoder, lidarId);
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(MAX_COORDINATES, 3);
    LidarSpacialProvider lidarSpacialProvider = new Mark8SpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    mark8Decoder.addRayListener(lidarSpacialProvider);
    mark8Decoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    mark8LcmClient.startSubscriptions();
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    laserPointCloud.fill(lidarRayBlockEvent.floatBuffer, lidarRayBlockEvent.byteBuffer);
  }

  @Override
  public void draw() {
    laserPointCloud.draw();
  }

  @Override
  public int size() {
    return laserPointCloud.size();
  }
}