package com.example



import scala.math.{acos, Pi}
import scalismo.common._
import scalismo.utils.Random.implicits._
import scalismo.io.{LandmarkIO, MeshIO, StatismoIO}
import java.io.File
import java.io._
import scala.math.{sqrt,pow}
import scalismo.common.PointWithId
import scalismo.mesh.TriangleMesh
import breeze.linalg._
import scalismo.geometry.{Landmark, Point, Point3D}
import scalismo.geometry._3D
import scalismo.ui.api.{ScalismoUI, StatisticalMeshModelViewControls}
import spire.random
import scalismo.{geometry, mesh}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util
import scala.util.Random

class SphereFitting (setLandmarkSphere : Seq[Landmark[_3D]]) {
  val nbLandmarks = setLandmarkSphere.length
  /* Let's build the 3*3 matrix according to the Matlab code the A matrix
   */
  //We also have to calculate the mean of the coordinates
  var meanX =0.0
  var meanY =0.0
  var meanZ =0.0
  var Xcoordinates = DenseVector.zeros[Double](nbLandmarks)
  var Ycoordinates = DenseVector.zeros[Double](nbLandmarks)
  var Zcoordinates = DenseVector.zeros[Double](nbLandmarks)
  var squareVector = DenseVector.zeros[Double](nbLandmarks)
  // First let's build the vectors containing all the x coordinates, y coordinates and z coordinates
  for (idLandmark <- 0 to nbLandmarks-1){
    var xCoordinates = setLandmarkSphere(idLandmark).point(0)
    var yCoordinates = setLandmarkSphere(idLandmark).point(1)
    var zCoordinates = setLandmarkSphere(idLandmark).point(2)
    Xcoordinates(idLandmark) = xCoordinates
    Ycoordinates(idLandmark) = yCoordinates
    Zcoordinates(idLandmark) = zCoordinates
    squareVector(idLandmark) = pow(xCoordinates, 2.0).+(pow(yCoordinates, 2.0)).+(pow(zCoordinates, 2.0))
    meanX = meanX.+(xCoordinates)
    meanY = meanY.+(yCoordinates)
    meanZ = meanZ.+(zCoordinates)
  }
  meanX = meanX./(nbLandmarks)
  meanY = meanY./(nbLandmarks)
  meanZ = meanZ./(nbLandmarks)

  var meanXvector = DenseVector.zeros[Double](nbLandmarks)
  meanXvector(0 to setLandmarkSphere.length.-(1)) := meanX

  var meanYvector = DenseVector.zeros[Double](nbLandmarks)
  meanYvector(0 to setLandmarkSphere.length.-(1)) := meanY

  var meanZvector = DenseVector.zeros[Double](nbLandmarks)
  meanZvector(0 to setLandmarkSphere.length.-(1)) := meanZ

  // We compute the 1,1 element of our matrix A
  val elemA11 = (Xcoordinates dot (Xcoordinates - meanXvector)).*(2.0./(nbLandmarks))
  // We compute the 1,2 (and 2,1 symetric matrix) element of our matrix A
  val elemA12 = (Xcoordinates dot (Ycoordinates - meanYvector)).*(2.0./(nbLandmarks))
  // We compute the 1,3  (and 3,1 symetric matrix)element of our matrix A
  val elemA13 = (Xcoordinates dot (Zcoordinates - meanZvector)).*(2.0./(nbLandmarks))
  // We compute the 2,2 element of our matrix A
  val elemA21 = (Ycoordinates dot (Xcoordinates - meanXvector)).*(2.0./(nbLandmarks))
  // We compute the 2,2 element of our matrix A
  val elemA22 = (Ycoordinates dot (Ycoordinates - meanYvector)).*(2.0./(nbLandmarks))
  // We compute the 2,3 (and 3,2 symetric matrix) element of our matrix A
  val elemA23 = (Ycoordinates dot (Zcoordinates - meanZvector)).*(2.0./(nbLandmarks))
  // We compute the 3,1 element of our matrix A
  val elemA31 = (Zcoordinates dot (Xcoordinates - meanXvector)).*(2.0./(nbLandmarks))
  // We compute the 3,2 element of our matrix A
  val elemA32 = (Zcoordinates dot (Ycoordinates - meanYvector)).*(2.0./(nbLandmarks))
  // We compute the 3,3 element of our matrix A
  val elemA33 = (Zcoordinates dot (Zcoordinates - meanZvector)).*(2.0./(nbLandmarks))

  val Amatrix = DenseMatrix((elemA11,elemA12,elemA13),
    (elemA21,elemA22,elemA23),(elemA31,elemA32,elemA33))

  /* Let's build the 1*3 matrix according to the Matlab code the B matrix
   */
  // We compute the 1,1 element of our matrix A
  val elemB11 = (squareVector dot (Xcoordinates - meanXvector))./(nbLandmarks)
  // We compute the 1,2 (and 2,1 symetric matrix) element of our matrix A
  val elemB21 = (squareVector dot (Ycoordinates - meanYvector))./(nbLandmarks)
  // We compute the 1,3  (and 3,1 symetric matrix)element of our matrix A
  val elemB31 = (squareVector dot (Zcoordinates - meanZvector))./(nbLandmarks)

  val Bmatrix = DenseMatrix((elemB11),(elemB21),(elemB31))
  /* Now we compute the center of the sphere
   */
  val center = (inv(Amatrix) * Bmatrix).data

  def centerSphere(): Landmark[_3D] ={
    var centerPoint = new Point3D(center(0),center(1),center(2))
    var centerLandmark = new Landmark[_3D]("iv", centerPoint)
    return centerLandmark
  }

  def radius(): Double ={
    var rrrad = 0.0
    for (i <- 0 to nbLandmarks-1){
      rrrad = rrrad.+(pow(Xcoordinates(i).-(center(0)),2.0))
      rrrad = rrrad.+(pow(Ycoordinates(i).-(center(1)),2.0))
      rrrad = rrrad.+(pow(Zcoordinates(i).-(center(2)),2.0))

    }
    rrrad = sqrt(rrrad./(nbLandmarks))
    return rrrad
  }
}
