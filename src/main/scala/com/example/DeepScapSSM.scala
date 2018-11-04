package com.example

import java.awt.Color

import com.github.tototoshi.csv._
import scalismo.common._
import scalismo.utils.Random.implicits._
import scalismo.io.{LandmarkIO, MeshIO, StatismoIO}
import java.io.File
import java.io._

import scalismo.common.PointWithId
import scalismo.mesh.{TriangleMesh, TriangleMesh3D }
import scalismo.mesh.boundingSpheres.{BoundingSpheres, Sphere}

import scalismo.geometry.{Landmark, Point}
import scalismo.geometry._3D
import scalismo.geometry.Point3D
import scalismo.ui.api.{ScalismoUI, StatisticalMeshModelViewControls}
import spire.random
import scalismo.mesh
import scalismo.statisticalmodel.StatisticalMeshModel

import scala.collection.mutable.ListBuffer
import scala.util
import scala.util.Random


object DeepScapSSM {

  def main(args: Array[String]) {

    // required to initialize native libraries (VTK, HDF5 ..)
    scalismo.initialize()

    // Your application code goes below here. Below is a dummy application that reads a mesh and displays it

    // create a visualization window
    val ui = ScalismoUI()

    // read a model from statistical shape model
    val scapM = StatismoIO.readStatismoMeshModel(new File("C:/Users/la  cage/Downloads/scapulaPCAmodel.h5")).get
    // take the mean mesh as reference
    val meanRefMesh = scapM.mean

    // load the landmarks for the scapulae (json file)
    // add them on the scapulae model
    val numberSamples = 1000
    var a = 0
    for (a <- 1 to numberSamples) {}
    val gaussianProcess = scapM.gp
    val scapLandmarks = LandmarkIO.readLandmarksJson[_3D](new File("C:/Users/la  cage/Documents/firstTryScap.json")).get
    val setPoint = meanRefMesh.pointSet
    val seqPtsLM = Seq[PointId](setPoint.findClosestPoint(scapLandmarks(0).point).id,
      setPoint.findClosestPoint(scapLandmarks(1).point).id, setPoint.findClosestPoint(scapLandmarks(2).point).id,
      setPoint.findClosestPoint(scapLandmarks(3).point).id, setPoint.findClosestPoint(scapLandmarks(4).point).id,
      setPoint.findClosestPoint(scapLandmarks(5).point).id, setPoint.findClosestPoint(scapLandmarks(6).point).id,
      setPoint.findClosestPoint(scapLandmarks(7).point).id, setPoint.findClosestPoint(scapLandmarks(8).point).id,
      setPoint.findClosestPoint(scapLandmarks(9).point).id, setPoint.findClosestPoint(scapLandmarks(10).point).id,
      setPoint.findClosestPoint(scapLandmarks(11).point).id, setPoint.findClosestPoint(scapLandmarks(12).point).id)

    val csvDatabase = new File("C:/Users/la  cage/Documents/scapFeaturesData.csv")
    val writer = CSVWriter.open(csvDatabase)
    writer.writeRow(List("MeshID", "CSA", "Version", "Tilt", "Glene Width", "Glene Length", "First PC", "Second PC", "Third PC", "Fourth PC", "Fifth PC", "Sixth PC", "Seventh PC", "Eighth PC", "Ninth PC", "Tenth PC"))
    var meshID = 0
    /* We iterate on a certain number of mesh to obtain a database on which we can work
     */
    for (a <- 1 to numberSamples) {
      /* We sample a new mesh from our SSM as a Discrete Field from which we can extract the PCA coefficients
       we then convert the DiscreteFields into a TriangleMesh to measure the features we need.
     */
      var discreteFSample = scapM.gp.sample
      var newPoints = discreteFSample.pointsWithValues.map { case (pt, v) => pt + v }
      var randomMesh = TriangleMesh3D(UnstructuredPointsDomain(newPoints.toIndexedSeq), scapM.referenceMesh.triangulation)
      var newsetPoint = randomMesh.pointSet
      var newLM = Seq[Landmark[_3D]](Landmark("GL", newsetPoint.point(seqPtsLM(0))), Landmark("GR", newsetPoint.point(seqPtsLM(1))),
        Landmark("AI", newsetPoint.point(seqPtsLM(2))), Landmark("PC", newsetPoint.point(seqPtsLM(3))),
        Landmark("GS", newsetPoint.point(seqPtsLM(4))), Landmark("GI", newsetPoint.point(seqPtsLM(5))),
        Landmark("AA", newsetPoint.point(seqPtsLM(6))), Landmark("MAA", newsetPoint.point(seqPtsLM(7))),
        Landmark("LAA", newsetPoint.point(seqPtsLM(8))),Landmark("PA", newsetPoint.point(seqPtsLM(9))),
        Landmark("TS", newsetPoint.point(seqPtsLM(10))),Landmark("SA", newsetPoint.point(seqPtsLM(11))),
        Landmark("GC", newsetPoint.point(seqPtsLM(12))))

      /* Visualization

      ui.show(newLM, "newScapLM")
      ui.show(randomMesh, "rsn") */

      val sdsds = new SetLandmarks(randomMesh)
      var info = sdsds.practicalMethod()
      println(info.deep.mkString("\n"))
      val coeffPCA = scapM.coefficients(randomMesh)
      //print(coeffPCA)
      info.:+(meshID)
      writer.writeRow(List(meshID) ++ info.toList ++ List(coeffPCA(0), coeffPCA(1), coeffPCA(2), coeffPCA(3), coeffPCA(4),coeffPCA(5), coeffPCA(6),coeffPCA(7), coeffPCA(8), coeffPCA(9)))
      meshID = meshID.+(1)
      print(info.toList)
    }
    writer.close()
    // change its color
    //meshView.color = Color.PINK
    val testMesh = MeshIO.readMesh(new File("C:/Users/la  cage/Documents/testMesh.stl")).get
    val ssss = new SetLandmarks(testMesh)
    print("dbzhdbhzdbh",ssss.practicalMethod().toSeq)

    ui.show(meanRefMesh, "newScapLM")
    ui.show(scapLandmarks, "rsn")
    /*
    // required to initialize native libraries (VTK, HDF5 ..)
    scalismo.initialize()

    // Your application code goes below here. Below is a dummy application that reads a mesh and displays it

    // create a visualization window
    val ui = ScalismoUI()

    // read a model from statistical shape model
    val scapM = StatismoIO.readStatismoMeshModel(new File("C:/Users/la  cage/Downloads/scapulaPCAmodel.h5")).get
    // take the mean mesh as reference
    val meanRefMesh = scapM.mean

    ui.show(meanRefMesh, "newScapLM")
    val scapLandmarks = LandmarkIO.readLandmarksJson[_3D](new File("C:/Users/la  cage/Documents/sphereGleneLm.json")).get

    var sphereFit = new SphereFitting(scapLandmarks)
    #print(sphereFit.radius(),sphereFit.centerSphere().toSeq,sphereFit.meanX,sphereFit.meanY,sphereFit.Zcoordinates - sphereFit.meanZvector)
    centerLandmark = sphereFit.centerSphere()

    var newS = scapLandmarks.+:(centerLandmark)
    ui.show(newS, "rsn")
  */
  }
}
